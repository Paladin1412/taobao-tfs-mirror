/**
 * 
 */
package com.taobao.common.tfs.nativetest;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.taobao.common.tfs.NativeTfsBaseCase;
import com.taobao.common.tfs.NativeTfsBaseCase.PlanType;
import com.taobao.gaia.AppCluster;
import com.taobao.gaia.AppGrid;
import com.taobao.gaia.AppServer;
import com.taobao.gaia.KillTypeEnum;

/**
 * @author Administrator/mingyan
 * 
 */
public class Function_ns_test extends NativeTfsBaseCase {
	public static final String strOnTimeReportLog = "report block on time";

	@Test
	public void Function_01_happy_path() {

		boolean bRet = false;
		caseName = "Function_01_happy_path";
		log.info(caseName + "===> start");

		/* Set loop flag */
		bRet = setSeedFlag(LOOPON);
		Assert.assertTrue(bRet);

		/* Set seed size */
		bRet = setSeedSize(1);
		Assert.assertTrue(bRet);

		/* Set unlink ratio */
		bRet = setUnlinkRatio(50);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* sleep */
		sleep(20);

		/* Stop write process */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check rate */
		bRet = checkRateEnd(SUCCESSRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = readCmd();
		Assert.assertTrue(bRet);

		/* Monitor the read process */
		bRet = readCmdMon();
		Assert.assertTrue(bRet);

		/* Check rate */
		bRet = checkRateEnd(SUCCESSRATE, READ);
		Assert.assertTrue(bRet);

		/* Unlink file */
		bRet = unlinkCmd();
		Assert.assertTrue(bRet);

		/* Monitor the unlink process */
		bRet = unlinkCmdMon();
		Assert.assertTrue(bRet);

		/* Check rate */
		bRet = checkRateEnd(SUCCESSRATE, UNLINK);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_02_one_ds_out_copy_block() {

		boolean bRet = false;
		caseName = "Function_02_one_ds_out_copy_block";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait 20s for recover */
		sleep(20);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check multi replicated block */
		bRet = chkMultiReplicatedBlock();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		/* Start the killed ds */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_03_all_ds_out_one_side() {

		boolean bRet = false;
		caseName = "Function_03_all_ds_out_one_side";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = killAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Wait 20s for recover */
		sleep(20);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* Start */
		bRet = startAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_04_all_ds_out() {

		boolean bRet = false;
		caseName = "Function_04_all_ds_out";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Kill all ds */
		bRet = killAllDs();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(0);
		Assert.assertTrue(bRet);

		/* Start */
		bRet = startAllDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_05_one_ds_in() {

		boolean bRet = false;
		caseName = "Function_05_one_ds_in";
		log.info(caseName + "===> start");

		/* Kill one ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* One ds in */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_06_all_ds_one_side_in() {

		boolean bRet = false;
		caseName = "Function_06_all_ds_one_side_in";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = killAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* All ds one side in */
		bRet = startAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_07_all_ds_in() {

		boolean bRet = false;
		caseName = "Function_07_all_ds_in";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = killAllDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(0);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* All ds one side in */
		bRet = startAllDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_08_one_clean_ds_in() {

		boolean bRet = false;
		caseName = "Function_08_one_clean_ds_in";
		log.info(caseName + "===> start");

		/* Clean one ds */
		bRet = cleanOneDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* One ds in */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Wait for move */
		sleep(100);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_09_all_clean_ds_one_side_in() {

		boolean bRet = false;
		caseName = "Function_09_all_clean_ds_one_side_in";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = cleanAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* All ds one side in */
		bRet = startAllDsOneSide();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_10_all_clean_ds_in() {

		boolean bRet = false;
		caseName = "Function_10_all_clean_ds_in";
		log.info(caseName + "===> start");

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = cleanAllDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(0);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(FAILRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* All ds one side in */
		bRet = startAllDs();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	@Test
	public void Function_11_compact() {

		boolean bRet = false;
		caseName = "Function_11_compact";
		log.info(caseName + "===> start");

		/* Set unlink ratio */
		bRet = setUnlinkRatio(90);
		Assert.assertTrue(bRet);

		/* Start write cmd */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Wait for compact */
		sleep(3600);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check the final rate */
		bRet = checkRateEnd(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		/* Set unlink ratio */
		bRet = setUnlinkRatio(50);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	@Test
	public void Function_12_rep_block() {

		boolean bRet = false;
		caseName = "Function_12_rep_block";
		log.info(caseName + "===> start");

		/* Modify MinReplication */
		bRet = setMinReplication(1);
		Assert.assertTrue(bRet);

		/* Modify MaxReplication */
		bRet = setMaxReplication(1);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(1);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Modify MaxReplication */
		bRet = setMaxReplication(3);
		Assert.assertTrue(bRet);

		/* Wait 10s for copy */
		sleep(10);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(2);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		/* Modify MinReplication */
		bRet = setMinReplication(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Modify MinReplication */
		bRet = setMaxReplication(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	@Test
	public void Function_13_write_mass_blocks() {

		boolean bRet = false;
		caseName = "Function_13_write_mass_blocks";
		log.info(caseName + "===> start");

		/* Get current used cap(before write) */
		HashMap<String, Double> usedCap = new HashMap<String, Double>();
		bRet = getUsedCap(usedCap);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		double chkValue = usedCap.get("Before") + CHK_WRITE_AMOUNT;
		bRet = chkUsedCap(chkValue);
		Assert.assertTrue(bRet);

		/* Stop write process */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check rate */
		bRet = checkRateEnd(SUCCESSRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// case 14-24 are suitable for new feature added since 1.3.3
	// @Test
	public void Function_14_one_ds_out_emerge_rep_block() {

		boolean bRet = false;
		caseName = "Function_14_one_ds_out_emerge_rep_block";
		log.info(caseName + "===> start");

		/* Set loop flag */
		bRet = setSeedFlag(LOOPON);
		Assert.assertTrue(bRet);

		/* Set seed size */
		bRet = setSeedSize(1);
		Assert.assertTrue(bRet);

		/* Set unlink ratio */
		bRet = setUnlinkRatio(50);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Kill one ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait 10s for recover */
		sleep(10);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check dump plan log */
		bRet = checkPlan(PlanType.PLAN_TYPE_EMERG_REPLICATE, BLOCK_CHK_TIME);
		Assert.assertTrue(bRet);

		/* Check multi replicated block */
		bRet = chkMultiReplicatedBlock();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_15_rep_block() {

		boolean bRet = false;
		caseName = "Function_15_rep_block";
		log.info(caseName + "===> start");

		/* Modify MinReplication */
		bRet = setMinReplication(1);
		Assert.assertTrue(bRet);

		/* Modify MaxReplication */
		bRet = setMaxReplication(1);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(1);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Modify MaxReplication */
		bRet = setMaxReplication(3);
		Assert.assertTrue(bRet);

		/* Wait 10s for copy */
		sleep(10);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Check block copys */
		bRet = chkBlockCntBothNormal(2);
		Assert.assertTrue(bRet);

		/* Check dump plan log */
		bRet = checkPlan(PlanType.PLAN_TYPE_REPLICATE, BLOCK_CHK_TIME);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		/* Modify MinReplication */
		bRet = setMinReplication(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_16_move_block() {

		boolean bRet = false;
		caseName = "Function_16_move_block";
		log.info(caseName + "===> start");

		/* Set loop flag */
		bRet = setSeedFlag(LOOPON);
		Assert.assertTrue(bRet);

		/* Set seed size */
		bRet = setSeedSize(100);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Modify balance_max_diff_block_num */
		bRet = setBalanceMaxDiffBlockNum(1);
		Assert.assertTrue(bRet);

		/* Wait 120s for write */
		sleep(120);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check dump plan log */
		bRet = checkPlan(PlanType.PLAN_TYPE_MOVE, BLOCK_CHK_TIME);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_17_compact_block() {

		boolean bRet = false;
		caseName = "Function_17_compact_block";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Compact block */
		bRet = compactBlock();
		Assert.assertTrue(bRet);

		/* Check dump plan log */
		bRet = checkPlan(PlanType.PLAN_TYPE_COMPACT, BLOCK_CHK_TIME);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	@Test
	public void Function_18_delete_block() {

		boolean bRet = false;
		caseName = "Function_18_delete_block";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Kill the 1st ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait 10s for ssm to update the latest info */
		sleep(10);

		/* Wait for completion of replication */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Start the killed ds */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Wait 10s for ssm to update the latest info */
		sleep(10);

		/* Wait for completion of deletion */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* TODO chk block distribute */
		// bRet = chkFinalRetSuc();
		// Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_19_one_ds_out_clear_plan() {

		boolean bRet = false;
		caseName = "Function_19_one_ds_out_clear_plan";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Kill the 1st ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Rotate ns log */
		bRet = rotateLog();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Check interrupt */
		bRet = checkInterrupt(1);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_20_one_ds_join_clear_plan() {

		boolean bRet = false;
		caseName = "Function_20_one_ds_join_clear_plan";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Kill the 1st ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Rotate ns log */
		bRet = rotateLog();
		Assert.assertTrue(bRet);

		/* Start the 1st ds */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Check interrupt */
		bRet = checkInterrupt(1);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_21_one_ds_restart_clear_plan() {

		boolean bRet = false;
		caseName = "Function_21_one_ds_restart_clear_plan";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Rotate ns log */
		bRet = rotateLog();
		Assert.assertTrue(bRet);

		/* Kill the 1st ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Start the 1st ds */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(10);

		/* Check interrupt */
		bRet = checkInterrupt(2);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_22_new_elect_writable_block_algorithm() {

		boolean bRet = false;
		caseName = "Function_22_new_elect_writable_block_algorithm";
		log.info(caseName + "===> start");

		/* Wait */
		sleep(60);

		/* Get the block num hold by each ds before write */
		HashMap<String, Integer> blockDisBefore = new HashMap<String, Integer>();
		bRet = getBlockDistribution(blockDisBefore);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Start sar to account network traffic */
		ArrayList<String> ds_ip_list = new ArrayList<String>();
		bRet = networkTrafMonStart(SAMPLE_INTERVAL,
				TEST_TIME / SAMPLE_INTERVAL, ds_ip_list);
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(TEST_TIME);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateEnd(SUCCESSRATE, WRITEONLY);
		Assert.assertTrue(bRet);

		/* Wait for completion of replication */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Check network traffic balance */
		bRet = chkNetworkTrafBalance("eth0", RXBYTPERSEC_SD_COL, ds_ip_list);
		Assert.assertTrue(bRet);

		/* Get the block num hold by each ds after write */
		HashMap<String, Integer> blockDisAfter = new HashMap<String, Integer>();
		bRet = getBlockDistribution(blockDisAfter);
		Assert.assertTrue(bRet);

		/* Check new added block balance status */
		bRet = checkWriteBalanceStatus(blockDisBefore, blockDisAfter);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_23_check_read_network_traffic_balance() {

		boolean bRet = false;
		caseName = "Function_23_check_read_network_traffic_balance";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		sleep(20);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Set loop flag */
		bRet = setReadFlag(LOOPON);
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = readCmd();
		Assert.assertTrue(bRet);

		/* Start sar to account network traffic */
		ArrayList<String> ds_ip_list = new ArrayList<String>();
		bRet = networkTrafMonStart(SAMPLE_INTERVAL,
				TEST_TIME / SAMPLE_INTERVAL, ds_ip_list);
		Assert.assertTrue(bRet);

		/* Wait */
		sleep(TEST_TIME);

		/* Stop cmd */
		bRet = readCmdStop();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateEnd(SUCCESSRATE, READ);
		Assert.assertTrue(bRet);

		/* Check network traffic balance */
		bRet = chkNetworkTrafBalance("eth0", TXBYTPERSEC_SD_COL, ds_ip_list);
		Assert.assertTrue(bRet);

		/* Set loop flag */
		bRet = setReadFlag(LOOPOFF);
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	// @Test
	public void Function_24_modify_gc_wait_time() {

		boolean bRet = false;
		caseName = "Function_24_modify_gc_wait_time";
		log.info(caseName + "===> start");

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Modify object_dead_max_time */
		bRet = setObjectDeadMaxTime(10);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Modify object_dead_max_time */
		bRet = setObjectDeadMaxTime(7200);
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Modify object_dead_max_time */
		bRet = setObjectDeadMaxTime(3600);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	/*
	 * [����] 1.����10��ds��ns���û㱨�̸߳���Ϊ1���㱨����Ϊ1�� �㱨ʱ������ý϶̣�5���ӣ���ns��ds������valgrind����
	 * 2.ʹ����������ķ�������ns��ds֮��Ĵ���
	 * 
	 * [��֤] ds�Ļ㱨������� [��ע]
	 */
	@Test
	public void test_3() {
		boolean bRet = false;
		changeNsConf(tfsGrid, "safe_mode_time", String.valueOf(5 * 60));
		startOneGrid(tfsGrid);

		for (int i = DSINDEX; i <= DS_CLUSTER_NUM; i++) {
			AppCluster appCluster = tfsGrid.getCluster(i);

			for (AppServer appServer : appCluster.getServerList()) {
				bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 50, 50);
				assertTrue(bRet);
			}
		}
		/* verify - check the report log in nameserver.log */
		assertTrue(checkOnTimeReportLog(MASTERIP));
	}

	/*
	 * 1.����10��ds������9��ds��һ̨���������1��ds��������һ̨�������ns����1���㱨�̣߳�1���㱨���У�������Ϊ2��
	 * ns����max_write_file_countΪ10�� ����block_max_sizeΪ10M�����ö�ʱ�㱨ʱ����Ϊ5����
	 * 2.����ns����ֻ̨��1��ds���������������� 3.�����ͻ��˲��Թ���ָ��blockId������block��־λ���ļ���ִ��д������д��λΪ1M
	 */
	@Test
	public void test_4() {
		boolean bRet = false;

		/* set max_write_filecount to 10 */
		changeNsConf(tfsGrid, "max_write_filecount", String.valueOf(10));
		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1 << 20)));
		startOneGrid(tfsGrid);

		AppServer appServer = tfsGrid.getCluster(DS_CLUSTER_NUM).getServer(0);
		bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 5, 5);
		assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Wait for completion of deletion */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* TODO chk block distribute */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;

	}

	/*
	 * 1.����4��ds��ds����������ͬ��������Ϊ2������max_write_filecountΪ10������block_max_sizeΪ10MB
	 * 2.�����ͻ��˲��Թ���ִ��д������д��λΪ1M�������ϳ�ʱ��
	 */

	@Test
	public void test_5() {
		boolean bRet = false;

		/* set max_write_filecount to 10 */
		changeNsConf(tfsGrid, "max_write_filecount", String.valueOf(10));

		/* Check block copys */
		bRet = chkBlockCntBothNormal(2);
		Assert.assertTrue(bRet);

		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1 << 20)));
		startOneGrid(tfsGrid);

		AppServer appServer = tfsGrid.getCluster(DS_CLUSTER_NUM).getServer(0);
		bRet = Proc.netBlockBase(MASTERIP, appServer.getIp(), 5, 5);
		assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		sleep(360);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	/*
	 * 1. ��delete�����balance����رգ�����4��ds��
	 * ����2��ds��������������1��������������1.5����һ��������������50%��������Ϊ2��
	 * ����max_write_filecountΪ10������block_max_sizeΪ10MB 2.
	 * �����ͻ��˲��Թ���ִ��д������д��λΪ1M�������ϳ�ʱ��
	 */

	@Test
	public void test_6() {
		boolean bRet = false;
		/* set delete to close */

		/* set balance to close */

		/* set max_write_filecount to 10 */
		changeNsConf(tfsGrid, "max_write_filecount", String.valueOf(10));

		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1 << 20)));
		bRet = chkBlockCntBothNormal(2);
		Assert.assertTrue(bRet);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);
		
		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);
	
		/* TODO chk block distribute */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;
	}

	/*
	 * 1.��delete����رգ�����4��ds������2��ds��������������1��������������1.5����1��������������50%��ds��ip�ֲ�Ϊ1:3��
	 * ������������ds����һ��ip������3��dsʹ����ͬ��ip��
	 * ������Ϊ2������Ǩ�Ʋ���balance_max_diff_block_num_Ϊ1������block_max_sizeΪ10MB
	 * 2.�����ͻ��˲��Թ���ִ��д������д��λΪ1M�������ϳ�ʱ��
	 */

	@Test
	public void test_7() {
		boolean bRet = false;

		/* set delete to closed */

		/* set mount count */
		// 2��ds��������������1��������������1.5����1��������������50%
		changeDsConf(tfsGrid, "mount_maxsize", String.valueOf(2097152));

		/* set ip 1:3 */

		/* set block copy */

		bRet = chkBlockCntBothNormal(2);
		Assert.assertTrue(bRet);
		
		/* set balance_max_diff_block_num to 1 */
        changeDsConf(tfsGrid, "balance_max_diff_block_num", String.valueOf(1) );
        Assert.assertTrue(bRet);

		/* set block_max_size to 10M */
		changeDsConf(tfsGrid, "block_max_size", String.valueOf(10 * (1 << 20)));
		startOneGrid(tfsGrid);

		/* Write file */
		bRet = writeCmd();
		Assert.assertTrue(bRet);

		/* Check the rate of write process */
		bRet = checkRateRun(SUCCESSRATE, WRITEONLY | READ | UNLINK);
		Assert.assertTrue(bRet);

		/* Stop write cmd */
		bRet = writeCmdStop();
		Assert.assertTrue(bRet);

		/* Wait 10s for ssm to update the latest info */
		sleep(10);

		/* Wait for completion of deletion */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* TODO chk block distribute */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		// ���1.���ns��־��Ӧ�úܿ���ܿ���move����Ĵ���������move��Դds�ǵ���������������̨ds��Ŀ��ds���������������������Ǹ�ds
		// 2.move֮��Դds�ϵ�blockɾ����Ŀ��ds�������˸�block

		log.info(caseName + "===> end");
		return;
	}

	/*
	 * 1. ���ø�����Ϊ2/2,8��ds��4��ds/1������� 2. kill��һ̨ds������block��������Ϊ1������block���� 3.
	 * �ȴ�blockȫ�����Ƴ�2������֮��������kill��ds����ʱ�򲿷�block��Ϊ3 4. �ȴ����ั��ɾ����ϣ���鸱���ֲ�
	 */

	@Test
	public void test_8() {

		boolean bRet = false;
		caseName = "test_8";
		log.info(caseName + "===> start");

		/* Kill the 1st ds */
		bRet = killOneDs();
		Assert.assertTrue(bRet);

		/* Wait for completion of replication */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* Start the killed ds */
		bRet = startOneDs();
		Assert.assertTrue(bRet);

		/* Wait 10s for ssm to update the latest info */
		sleep(10);

		/* Wait for completion of deletion */
		bRet = chkBlockCntBothNormal(BLOCKCOPYCNT);
		Assert.assertTrue(bRet);

		/* TODO chk block distribute */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		/* Read file */
		bRet = chkFinalRetSuc();
		Assert.assertTrue(bRet);

		log.info(caseName + "===> end");
		return;

	}

	private void changeNsConf(AppGrid appGrid, String strFieldName,
			String strValue) {
		boolean bRet = false;
		for (AppServer appServer : appGrid.getCluster(NSINDEX).getServerList()) {
			String strNsConf = appServer.getConfname();
			bRet = conf.confReplaceSingle(appServer.getIp(), strNsConf,
					strFieldName, strValue);
			assertTrue(bRet);
		}
	}

	private void changeDsConf(AppGrid appGrid, String strFieldName,
			String strValue) {
		boolean bRet = false;

		for (int i = 1; i <= DS_CLUSTER_NUM; i++) {
			AppCluster appCluster = appGrid.getCluster(i);
			for (AppServer appServer : appCluster.getServerList()) {
				String strDsConf = appServer.getDir() + appServer.getConfname();
				bRet = conf.confReplaceSingle(appServer.getIp(), strDsConf,
						strFieldName, strValue);
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

	private boolean checkOnTimeReportLog(String targetIp) {
		ArrayList<String> result = new ArrayList<String>(500);
		String strCmd = "cat " + TFS_HOME + "/logs/nameserver.log | grep "
				+ strOnTimeReportLog;

		if (Proc.proStartBase(targetIp, strCmd, result) == false) {
			log.error("Failed to checkOnTimeReportLog!");
			return false;
		}

		if (result.size() > 0)
			return true;

		return false;
	}

	@After
	public void tearDown() {
		boolean bRet = false;

		/* Stop all client process */
		bRet = allCmdStop();
		Assert.assertTrue(bRet);

		/* Move the seed file list */
		bRet = mvSeedFile();
		Assert.assertTrue(bRet);

		/* Clean the caseName */
		caseName = "";
	}

	@Before
	public void setUp() {
		boolean bRet = false;

		/* Reset case name */
		caseName = "";

		if (!grid_started) {
			/* Set the failcount */
			bRet = setAllFailCnt();
			Assert.assertTrue(bRet);

			/* Kill the grid */
			bRet = tfsGrid.stop(KillTypeEnum.FORCEKILL, WAITTIME);
			Assert.assertTrue(bRet);

			/* Set Vip */
			bRet = migrateVip();
			Assert.assertTrue(bRet);

			/* Clean the log file */
			bRet = tfsGrid.clean();
			Assert.assertTrue(bRet);

			bRet = tfsGrid.start();
			Assert.assertTrue(bRet);

			/* Set failcount */
			bRet = resetAllFailCnt();
			Assert.assertTrue(bRet);

			grid_started = true;
		}

	}
}
