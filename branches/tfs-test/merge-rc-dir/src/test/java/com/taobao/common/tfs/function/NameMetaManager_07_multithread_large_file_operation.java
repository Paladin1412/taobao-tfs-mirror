package com.taobao.common.tfs.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner; 
import net.sourceforge.groboutils.junit.v1.TestRunnable;  

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.taobao.common.tfs.function.tfsNameBaseCase;
import com.taobao.common.tfs.namemeta.FileMetaInfo;
import com.taobao.common.tfs.namemeta.NameMetaManager;
import com.taobao.common.tfs.namemeta.NameMetaManager;
public class NameMetaManager_07_multithread_large_file_operation  extends  tfsNameBaseCase {

    public static NameMetaManager MetaManager1 = new NameMetaManager();
    public static NameMetaManager MetaManager2 = new NameMetaManager();
	public static List<FileMetaInfo>  file_info    = null;
	public static boolean suc_flag=false;
    @BeforeClass
	public  static void setUp() throws Exception {
     
	 	
	}
	@AfterClass
	public static void tearDown() throws Exception 
	{
	}
    @Before
	public  void Before()  {
		 	
    	Assert.assertTrue(MetaManager1.createDir(appId, userId,"/public"));
	}
	@After
	public  void After() 
	{
		MetaManager1.createDir(appId, userId,"/public");
	}
	@Test
	public void test_01_multi_threads_create_and_save_different_large_files() throws Throwable {

		TestRunnable tr1 ,tr2 ,tr3,tr4;
		tr1 = new Thread1();
		tr2 = new Thread1();
		tr3 = new Thread1();
        tr4 = new Thread1();
		TestRunnable[] trs = {tr1,tr2,tr3,tr4};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
	}
	
	@Test
	public void test_02_multi_threads_rename_differernt_large_files_in_same_dir() throws Throwable {

		TestRunnable tr1 ,tr2, tr3;
		tr1 = new Thread2();
		tr2 = new Thread2();
		tr3 = new Thread2();


		TestRunnable[] trs = {tr1,tr2,tr3};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
	}
	
	@Test
	public void test_03_multi_threads_move_different_files_to_same_dir() throws Throwable {

		TestRunnable tr1 ,tr2, tr3 ;
		tr1 = new Thread3();
		tr2 = new Thread3();
		tr3 = new Thread3();
		TestRunnable[] trs = {tr1,tr2,tr3};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
	}
  
	 
	@Test
	public void test_04_multi_threads_create_and_save_and_delete_one_file() throws Throwable {

		TestRunnable tr1 ,tr2, tr3;
		tr1 = new Thread5();
		tr2 = new Thread5();
		tr3 = new Thread5();

		TestRunnable[] trs = {tr1 ,tr2, tr3};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
		
	}
    
	@Test
	public void test_05_multi_threads_move_one_directory() throws Throwable {

		TestRunnable tr1 ,tr2, tr3,tr4 ,tr5, tr6;
		tr1 = new Thread6();
		tr2 = new Thread6();
		tr3 = new Thread6();
		tr4 = new Thread6();
		tr5 = new Thread6();
		tr6 = new Thread6();
		TestRunnable[] trs = {tr1 ,tr2, tr3,tr4 ,tr5, tr6};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
	}
	
	@Test
	public void test_06_multi_threads_rename_one_directory() throws Throwable {

		TestRunnable tr1 ,tr2;
		tr1 = new Thread7();
		tr2 = new Thread7();
	
		TestRunnable[] trs = {tr1,tr2};
		MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		mttr.runTestRunnables();
		file_info = MetaManager1.lsDir(userId,"/public");
		Assert.assertEquals(file_info.size(), 0);
	}
	
	//Threads for every test case to call
    private class Thread1 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			create_different_files();
		}
	}
	private class Thread2 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			rename_different_files();
		}
	}
	private class Thread3 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			move_different_large_files();
		}
	}

    private class Thread5 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			create_and_save_and_delete_one_file();
		}
	}
    private class Thread6 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			move_one_file();
		}
	}
    private class Thread7 extends TestRunnable {
		@Override
		public void runTest() throws Throwable {
			//线程要调用的方法或者要执行的操作
			rename_one_file();
		}
	}
     
    
    public void create_different_files() throws Exception 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
		 boolean ret;

         String filepath =String.valueOf(Thread.currentThread().getId());
         
         log.info("filepath"+filepath);
         
		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.saveFile(userId,resourcesPath+"4M.jpg","/public/"+filepath+"/newfile");
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.rmFile(userId,"/public/"+filepath+"/newfile");
		 Assert.assertTrue(ret);

		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
		 Assert.assertTrue(ret);

	}
	public void create_and_save_and_delete_one_file() throws Exception 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
         //cannot verify the result of every single step ,do that in the last step
		 MetaManager1.createDir(appId, userId,"/public/bus");
         
		 MetaManager1.saveFile(userId,resourcesPath+"4M.jpg", "/public/bus/blogbus");
		 
         MetaManager1.rmFile(userId,"/public/bus/blogbus");
		 
         MetaManager1.createDir(appId, userId,"/public/bus");


	}
	public void rename_different_files() throws Exception 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
		 boolean ret;

         String filepath =String.valueOf(Thread.currentThread().getId());
         
         log.info("filepath"+filepath);
         
		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.saveFile(userId,resourcesPath+"4M.jpg","/public/"+filepath+"/newfile");
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.mvFile(userId,"/public/"+filepath+"/newfile","/public/"+filepath+"/renamed");
         Assert.assertTrue(ret);
		 
         ret = MetaManager1.rmFile(userId,"/public/"+filepath+"/renamed");
		 Assert.assertTrue(ret);

		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
		 Assert.assertTrue(ret);

	
	}
	public void rename_one_file() throws Exception 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
        
		 MetaManager1.createDir(appId, userId,"/public/test");
		 
		 MetaManager1.saveFile(userId,resourcesPath+"4M.jpg", "/public/test/bus");
         
         MetaManager1.mvFile(userId,"/public/test/bus","/public/test/renamed");
         
         MetaManager1.rmFile(userId,"/public/test/renamed");
         
         MetaManager1.createDir(appId, userId,"/public/test");
	}
	public void move_different_large_files() 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
		 boolean ret;

         String filepath =String.valueOf(Thread.currentThread().getId());
         
         log.info("filepath"+filepath);
         
		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.saveFile(userId,resourcesPath+"4M.jpg","/public/"+filepath+"/new"+filepath);
         Assert.assertTrue(ret);
         
		 ret = MetaManager1.mvFile(userId,"/public/"+filepath+"/new"+filepath,"/public/"+"new"+filepath);
         Assert.assertTrue(ret);
		 
         ret = MetaManager1.rmFile(userId,"/public/"+"new"+filepath);
		 Assert.assertTrue(ret);

		 ret = MetaManager1.createDir(appId, userId,"/public/"+filepath);
		 Assert.assertTrue(ret);

	}
	public void move_one_file() throws Exception 
	{    	 
		 log.info("线程===" + Thread.currentThread().getId() + "执行开始");
	        
		 MetaManager1.createDir(appId, userId,"/public/test");
		 
		 MetaManager1.saveFile(userId,resourcesPath+"4M.jpg", "/public/test/bus");
         
         MetaManager1.mvFile(userId,"/public/test/bus","/public/bus");
         
         MetaManager1.rmFile(userId,"/public/bus");
         
         MetaManager1.createDir(appId, userId,"/public/test");
		 
     
	}

}
