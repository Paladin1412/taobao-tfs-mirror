package com.taobao.common.tfs.function;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.gaia.AppCluster;
import com.taobao.gaia.AppGrid;
import com.taobao.gaia.AppServer;
import com.taobao.gaia.HelpBase;
import com.taobao.gaia.KillTypeEnum;

public class Function_report_on_time extends NativeTfsBaseCase {

	public HelpBase helpBase = new HelpBase();
	public static final String strOnTimeReportLog = "report block on time";
//
//	@Test
//	public void ns_report_ontime_under_writepress() {
//		boolean bRet = false;
//		caseName = "ns_report_ontime_safemodetimeEqualzero_with_writepress";
//		log.info("ns_report_ontime_safemodetimeEqualzero_with_writepress"
//				+ "===> start");
//
//		/* Write file */
//		bRet = writeCmd();
//		assertTrue(bRet);
//
//		String serverIP = "10.232.4.1";
//		String blkIp = "10.232.4.11";
//
//		/* Block net */
//		helpBase.netBlockBase(serverIP, blkIp, 1, 5);
//
//		/* wait 5s */
//		sleep(1000 * 5);
//
//		/* Stop write file */
//		bRet = writeCmdStop();
//		assertTrue(bRet);
//		helpBase.netUnblockBase(serverIP);
//
//		/* verification */
//	}
//
//	@Test
//	public void ns_report_safe_mode_time_equal_zero() {
//		boolean bRet = false;
//		caseName = " ns_report_safe_mode_time_equal_zero";
//		log.info(" ns_report_safe_mode_time_equal_zero" + "===> start");
//		String serverIP = "10.232.4.1";
//
//		/* set safe_mode_time=0 */
//		helpBase.confDelLineBase(serverIP, "tfsConf", "67");
//		helpBase.confAddLineBase(serverIP, "tfsConf", 67, "safe_mode_time =0");
//
//		/* Write file */
//		bRet = writeCmd();
//		assertTrue(bRet);
//
//		/* wait 5s */
//		sleep(1000 * 5);
//
//		/* Stop write file */
//		bRet = writeCmdStop();
//		assertTrue(bRet);
//
//		/* verification */
//
//	}

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
	1.����10��ds������9��ds��һ̨����������1��ds��������һ̨��������
	ns����1���㱨�̣߳�1���㱨���У�������Ϊ2��
	ns����max_write_file_countΪ10������block_max_sizeΪ10M�����ö�ʱ�㱨ʱ����Ϊ5����
	2.����ns����ֻ̨��1��ds�����������������
	3.�����ͻ��˲��Թ���ִ��д������д��λΪ1M
	
	[��֤]
	ÿ��5���ӣ����ֵ���ds�㱨ʱ���Կ���дʧ�ܣ��㱨����д�ɹ�
	[��ע]
	*/
	@Test
	public void test_1()
	{
		boolean bRet = false;

		/* set max_write_filecount to 10, safe_mode_time to 300s */
		changeNsConf(tfsGrid, "max_write_filecount", String.valueOf(10));
		changeNsConf(tfsGrid, "safe_mode_time", String.valueOf(5*60));
		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1<<20) ));
		startOneGrid(tfsGrid);
		
		AppServer appServer = tfsGrid.getCluster(DS_CLUSTER_NUM).getServer(0);
		bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 5, 5);
		assertTrue(bRet);

		/* write to */
		bRet = setSeedFlag(1);
		assertTrue(bRet);

		bRet = setSeedSize(1);
		assertTrue(bRet);
		
		/* Set unlink ratio */
		bRet = setUnlinkRatio(50);
		Assert.assertTrue(bRet);
		
		/* set write/read/unlink cluster addr */
		bRet = setClusterAddr(NSVIP + ":" + NSPORT);
		assertTrue(bRet);

		bRet = writeCmd();
		assertTrue(bRet);

		sleep(10*60);
		bRet = writeCmdStop();
		assertTrue(bRet);
		
		/* verify */

	}

	/*
	[����]
	1.����10��ds������9��ds��һ̨����������1��ds��������һ̨��������
	ns����1���㱨�̣߳�1���㱨���У�������Ϊ2��ns����max_write_file_countΪ10��
	����block_max_sizeΪ10M�����ö�ʱ�㱨ʱ����Ϊ5����
	2.����ns����ֻ̨��1��ds�����������������
	3.�����ͻ��˲��Թ���ָ��blockId������block��־λ���ļ���ִ��д������д��λΪ1M
	
	[��֤]
	ÿ��5���ӣ����ֵ���ds�㱨ʱ���Կ���дʧ�ܣ��㱨����д�ɹ�
	[��ע]
	*/
	@Test
	public void tes_2()
	{
		boolean bRet = false;

		/* set max_write_filecount to 10, safe_mode_time to 300s */
		changeNsConf(tfsGrid, "max_write_filecount", String.valueOf(10));
		changeNsConf(tfsGrid, "safe_mode_time", String.valueOf(5*60));
		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1<<20) ));
		startOneGrid(tfsGrid);
		
		AppServer appServer = tfsGrid.getCluster(DS_CLUSTER_NUM).getServer(0);
		bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 5, 5);
		assertTrue(bRet);

		/* write to */
		bRet = setSeedFlag(1);
		assertTrue(bRet);

		bRet = setSeedSize(1);
		assertTrue(bRet);
		
		/* Set unlink ratio */
		bRet = setUnlinkRatio(50);
		Assert.assertTrue(bRet);
		
		/* set write/read/unlink cluster addr */
		bRet = setClusterAddr(NSVIP + ":" + NSPORT);
		assertTrue(bRet);

		bRet = writeCmd();
		assertTrue(bRet);

		sleep(10*60);
		bRet = writeCmdStop();
		assertTrue(bRet);

		/* verify */
	}

	/*
	[����]
	1.����10��ds��ns���û㱨�̸߳���Ϊ1���㱨����Ϊ1��
	�㱨ʱ������ý϶̣�5���ӣ���ns��ds������valgrind����
	2.ʹ����������ķ�������ns��ds֮��Ĵ���
	
	[��֤]
	ds�Ļ㱨�������
	[��ע]
	*/
	@Test
	public void test_3()
	{
		boolean bRet = false;
		changeNsConf(tfsGrid, "safe_mode_time", String.valueOf(5*60));
		startOneGrid(tfsGrid);

		for(int i = DSINDEX; i <= DS_CLUSTER_NUM; i++ )
		{
			AppCluster appCluster = tfsGrid.getCluster(i);

			for(AppServer appServer:appCluster.getServerList())
			{
				bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 50, 50);
				assertTrue(bRet);
			}
		}
		/* verify - check the report log in nameserver.log */
		assertTrue(checkOnTimeReportLog(MASTERIP));
	}

	private void changeNsConf(AppGrid appGrid, String strFieldName, String strValue)
	{
		boolean bRet = false;
		for(AppServer appServer:appGrid.getCluster(NSINDEX).getServerList())
		{
			String strNsConf = appServer.getConfname();
			bRet = conf.confReplaceSingle(appServer.getIp(), strNsConf, strFieldName, strValue);
			assertTrue(bRet);			
		}
	}

	private void changeDsConf(AppGrid appGrid, String strFieldName, String strValue)
	{
		boolean bRet = false;

		for(int i = 1; i <= DS_CLUSTER_NUM; i ++)
		{
			AppCluster appCluster = appGrid.getCluster(i);
			for(AppServer appServer:appCluster.getServerList())
			{
				String strDsConf = appServer.getDir() + appServer.getConfname();				
				bRet = conf.confReplaceSingle(appServer.getIp(), strDsConf, strFieldName, strValue);
				assertTrue(bRet);			
			}
		}
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