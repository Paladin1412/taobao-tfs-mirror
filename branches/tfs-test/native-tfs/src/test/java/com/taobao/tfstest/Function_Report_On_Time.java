package com.taobao.tfstest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.gaia.AppGrid;
import com.taobao.gaia.HelpBase;
import com.taobao.gaia.KillTypeEnum;

public class Function_Report_On_Time extends FailOverBaseCase {

	public HelpBase helpBase = new HelpBase();
	public static final String strOnTimeReportLog = "report block on time";

	@Test
	public void ns_report_ontime_under_writepress() {
		boolean bRet = false;
		caseName = "ns_report_ontime_safemodetimeEqualzero_with_writepress";
		log.info("ns_report_ontime_safemodetimeEqualzero_with_writepress"
				+ "===> start");

		/* Write file */
		bRet = writeCmd();
		assertTrue(bRet);

		String serverIP = "10.232.4.1";
		String blkIp = "10.232.4.11";

		/* Block net */
		helpBase.netBlockBase(serverIP, blkIp, 1, 5);

		/* wait 5s */
		sleep(1000 * 5);

		/* Stop write file */
		bRet = writeCmdStop();
		assertTrue(bRet);
		helpBase.netUnblockBase(serverIP);

		/* verification */
	}

	@Test
	public void ns_report_safe_mode_time_equal_zero() {
		boolean bRet = false;
		caseName = " ns_report_safe_mode_time_equal_zero";
		log.info(" ns_report_safe_mode_time_equal_zero" + "===> start");
		String serverIP = "10.232.4.1";

		/* set safe_mode_time=0 */
		helpBase.confDelLineBase(serverIP, "tfsConf", "67");
		helpBase.confAddLineBase(serverIP, "tfsConf", 67, "safe_mode_time =0");

		/* Write file */
		bRet = writeCmd();
		assertTrue(bRet);

		/* wait 5s */
		sleep(1000 * 5);

		/* Stop write file */
		bRet = writeCmdStop();
		assertTrue(bRet);

		/* verification */

	}

	/*
	 * ���裺1.����ns�����еĶ�ʱ�㱨ʱ���� 2. ��ʱ�㱨��������
	 */
	@Test
	public void ontime_report_happy_path() {
		int safeModeTime = 10;
		caseName = "ns_switch_one_time_ontime";
		log.info(caseName + "===> start");
		String targetIp = MASTERIP;

		/* set safe_mode_time to 10 */
		conf.confReplaceSingle(targetIp, NSCONF, "safe_mode_time", String.valueOf(safeModeTime));
		startOneGrid(tfsGrid);

		/* wait one round of report */
		sleep(safeModeTime + 10);

		/* wait ns startup */
		sleep(10);

		/* verify - check the report log in nameserver.log */
		assertTrue(checkOnTimeReportLog(targetIp));
	}

	/*
	 * ���裺 1.����ns�����еĶ�ʱ�㱨ʱ����  2.��һ�ֻ㱨֮��ns�����л�  3.���л�֮��Ķ�ʱ�㱨���
	 */
	@Test
	public void ns_switch_one_time_ontime() {
		int safeModeTime = 10;
		caseName = "ns_switch_one_time_ontime";
		log.info(caseName + "===> start");
		String targetIp = MASTERIP;

		/* set safe_mode_time to 10 */
		conf.confReplaceSingle(targetIp, NSCONF, "safe_mode_time", String.valueOf(safeModeTime));
		startOneGrid(tfsGrid);

		/* wait one round of report */
		sleep(safeModeTime + 10);
		
		killMasterNs();
		startSlaveNs();
		
		/* wait ns startup */
		sleep(10);

		/* verify - check the report log in nameserver.log */
		assertTrue(checkOnTimeReportLog(targetIp));
	}

	/*
	1.ʹ��4��ds��ns����1���㱨�̣߳��㱨���д�СΪ1����ʱ�㱨ʱ������ý϶̣�5���ӣ�
	2.������1̨dsƵ���������ɴ�
	*/
	@Test
	public void ds_switch_some_time_ontime() {
		boolean bRet = false;
		int safeModeTime = 10;
		caseName = "ds_switch_some_time_ontime";
		log.info(caseName + "===> start");
		String targetIp = MASTERIP;

		/* set safe_mode_time to 10 */
		conf.confReplaceSingle(targetIp, NSCONF, "safe_mode_time", String.valueOf(safeModeTime));
		startOneGrid(tfsGrid);

		/* wait one round of report */
		sleep(safeModeTime + 10);

		for(int i = 0; i < 5; i++)
		{
			/* kill one ds */
			bRet = killOneDs();
			assertTrue(bRet);

			/* restart one ds */
			bRet = startOneDs();
			assertTrue(bRet);

			sleep(5);
		}

		/* verify - check the report log in nameserver.log */
		assertTrue(checkOnTimeReportLog(targetIp));
	}

	/*
	[����]
	1.����10��ds������9��ds��һ̨���������1��ds��������һ̨�������
	ns����1���㱨�̣߳�1���㱨���У�������Ϊ2��
	ns����max_write_file_countΪ10������block_max_sizeΪ10M��
	���ö�ʱ�㱨ʱ����Ϊ5����
	2.����ns����ֻ̨��1��ds����������������
	3.�����ͻ��˲��Թ���ָ��blockId������block��־λ���ļ���ִ��д������д��λΪ1M
	
	[��֤]
	ÿ��5���ӣ����ֵ���ds�㱨ʱ���Կ���дʧ�ܣ��㱨����д�ɹ�
	[��ע]
	*/
	@Test
	public void test_1()
	{
		
	}
	/*
	����
	[����]
	1.����4��ds��ds����������ͬ��������Ϊ2������max_write_filecountΪ10������block_max_sizeΪ10MB
	2.�����ͻ��˲��Թ���ִ��д������д��λΪ1M�������ϳ�ʱ��
	
	[��֤]
	д��Ϻ����ds��������block�����
	[��ע]
	*/
	@Test
	public void ds_situation_is_same_with_conditions()
	{
		int maxWriteFilecount = 10;
		boolean bRet = false;
		caseName = "ds_situation_is_same_with_conditions";
		log.info(caseName + "===> start");
		String targetIp = MASTERIP;


		/* set max_write_filecount and block_max_size */
		bRet = conf.confReplaceSingle(targetIp, NSCONF, "max_write_filecount", String.valueOf(maxWriteFilecount));
		assertTrue(bRet);

		/* both ds.conf and ns.conf exist block_max_size, which one will take effect */
		for(int i = DSINDEX; i <= DS_CLUSTER_NUM; i++)
		{
			for(int j = 0; j < tfsGrid.getCluster(i).getServerList().size(); j++)
			{
				String strDsConf = tfsGrid.getCluster(i).getServer(j).getDir() + 
							tfsGrid.getCluster(i).getServer(j).getConfname();
				targetIp = tfsGrid.getCluster(i).getServer(j).getIp();
				bRet = conf.confReplaceSingle(targetIp, strDsConf, "block_max_size", String.valueOf(maxWriteFilecount));
				assertTrue(bRet);
			}
		}

		/* set write file size to 1M */
		bRet = conf.confReplaceSingle(CLIENTIP, CLIENTCONF, "size", String.valueOf(1<<20));
		assertTrue(bRet);
	
		/* start grid */
		startOneGrid(tfsGrid);
		sleep(30);
		
		/* write for 500s */
		writeCmd();
		sleep(500);
		writeCmdStop();

		/* check block number */
		String strCmd;
		ArrayList<String> result = new ArrayList<String>();
		
		strCmd = TFS_HOME + "bin/ssm -s ";
		strCmd += MASTERIP + ":" + MASTERSER.getPort();
		strCmd += " -i 'machine -a'";
		bRet = Proc.cmdOutBase(CLIENTIP, strCmd, "[ 0-9]*", 5, null, result);
		assertTrue(bRet);
		
		for(int i = 0; i < result.size() - 1; i++)
		{
			assertEquals(Integer.parseInt(result.get(i))/10, Integer.parseInt(result.get(i+1))/10);
		}

	}
	/*
	����
	[����]
	1.����4��ds��ds����������ͬ��������Ϊ2������max_write_filecountΪ10������block_max_sizeΪ10MB
	2.�����ͻ��˲��Թ���ִ��д������д��λΪ1M�������ϳ�ʱ��
	
	[��֤]
	д��Ϻ����ds��������block�����
	[��ע]
	*/
	@Test
	public void test_3()
	{
		
	}
	
	private void startOneGrid(AppGrid appGrid) {
		boolean bRet = false;
		/* Set the failcount */
		bRet = setAllFailCnt();
		Assert.assertTrue(bRet);

		/* Kill the grid */
		bRet = appGrid.stop(KillTypeEnum.FORCEKILL, WAITTIME);
		Assert.assertTrue(bRet);

		/* Set Vip */
		bRet = migrateVip();
		Assert.assertTrue(bRet);

		/* Clean the log file */
		bRet = appGrid.clean();
		Assert.assertTrue(bRet);

		bRet = appGrid.start();
		Assert.assertTrue(bRet);

		/* Set failcount */
		bRet = resetAllFailCnt();
		Assert.assertTrue(bRet);
	}

	private boolean checkOnTimeReportLog(String targetIp)
	{
		ArrayList<String> result = new ArrayList<String>(500);
		String strCmd = "cat " + TFS_HOME + "/logs/nameserver.log | grep " + strOnTimeReportLog;

		if(Proc.proStartBase(targetIp, strCmd, result) == false)
		{
			log.error("Failed to checkOnTimeReportLog!");
			return false;
		}

		if(result.size() > 0)
			return true;

		return false;
	}

	@Before
	public void setUp() {
		/* Reset case name */
		caseName = "";
	}
	@After
	public void tearDown()
	{
		boolean bRet;

		bRet = tfsGrid.stop(KillTypeEnum.FORCEKILL, WAITTIME);
		assertTrue(bRet);
	}
}
