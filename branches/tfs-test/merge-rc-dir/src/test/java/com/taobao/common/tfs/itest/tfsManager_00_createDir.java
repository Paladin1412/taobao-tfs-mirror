package com.taobao.common.tfs.itest;

import java.util.Random;


import org.junit.Test;
import org.junit.Ignore;
import junit.framework.Assert;

import com.taobao.common.tfs.tfsNameBaseCase;



public class tfsManager_00_createDir extends tfsNameBaseCase 
{

    @Test
    public  void  test_01_createDir_right_filePath()
    {
    	boolean bRet;
	    log.info( "test_01_createDir_right_filePath" );
        bRet=tfsManager.createDir(appId, userId, "/textcreateDir1");
        Assert.assertTrue("Create Dir with right path should be true", bRet);
        tfsManager.rmDir(appId, userId, "/textcreateDir1");
     }
	
	@Test
	public void  test_02_createDir_leap_filePath()
	{
	   boolean bRet;
	   log.info( "test_02_createDir_leap_filePath" );
	   bRet=tfsManager.createDir(appId, userId, "/textcreateDir2/test");
	   Assert.assertFalse("Create Dir with leap path should be false", bRet);
	}
	@Test
	public void  test_03_createDir_double_time()
	{
	   boolean bRet = false;
	   log.info( "test_03_createDir_double_time" );

	   bRet=tfsManager.createDir(appId, userId, "/textcreateDir3dt"); 
	   Assert.assertTrue("Create Dir with right path should be true", bRet);
	   bRet=tfsManager.createDir(appId, userId, "/textcreateDir3dt");
	   Assert.assertFalse("Create Dir with two times should be false", bRet);

	   bRet = tfsManager.rmDir(appId, userId, "/textcreateDir3dt");
       Assert.assertTrue(bRet);
	}
	@Test
	public void  test_04_createDir_null_filePath()
	{
	   boolean bRet;
	   log.info( "test_04_createDir_null_filePath" );
	   bRet=tfsManager.createDir(appId, userId, null);
	   Assert.assertFalse("Create Dir with null path should be false", bRet);
	}
	@Test
	public void  test_05_createDir_empty_filePath()
	{
	   boolean bRet;
	   log.info( "test_04_createDir_null_filePath" );
	   bRet=tfsManager.createDir(appId, userId, "");
	   Assert.assertFalse("Create Dir with null path should be false", bRet);
	}
	@Test
	public void  test_06_createDir_with_same_fileName()
	{
	   boolean bRet;
	   log.info( "test_06_createDir_with_same_fileName" );
	   tfsManager.createFile(appId, userId, "/textcreateDir6wsfn");
	   bRet=tfsManager.createDir(appId, userId, "/textcreateDir6wsfn");
	   Assert.assertTrue("Create Dir with the same File name should be true", bRet);
	   tfsManager.rmFile(appId, userId, "/textcreateDir6wsfn");
	   tfsManager.rmDir(appId, userId, "/textcreateDir6wsfn");
	}
	@Test
	public void  test_07_createDir_wrong_filePath_1()
	{
	   boolean bRet;
	   log.info( "test_07_createDir_wrong_filePath_1" );
	   bRet=tfsManager.createDir(appId, userId, "textcreateDir7wfp");
	   Assert.assertFalse("Create Dir with wrong_1 path should be false", bRet);
	}
	@Test
	public  void  test_08_createDir_wrong_filePath_2()
	{
	   boolean bRet;
	   log.info( "test_08_createDir_wrong_filePath_2" );
	   bRet=tfsManager.createDir(appId, userId, "////textcreateDir8wfp////");
	   Assert.assertTrue("Create Dir with wrong_2 path be true", bRet);
	   tfsManager.rmDir(appId, userId, "/textcreateDir8wfp");
	}
	@Test
	public void  test_09_createDir_wrong_filePath_3()
	{
	   boolean bRet;
	   log.info( "test_09_createDir_wrong_filePath_3" );
	   bRet=tfsManager.createDir(appId, userId, "/");
	   Assert.assertFalse("Create Dir with wrong_3 path should be false", bRet);
	}
	@Test
	public void  test_10_createDir_width_100()
	{
	   boolean bRet;
	   log.info( "test_10_createDir_width_100" );
	   int i;
	   for(i=1;i<=100;i++)
	   {
	       bRet=tfsManager.createDir(appId, userId, "/textcreateDir10w"+i);
	       Assert.assertTrue("Create textcreateDir10w"+i+" should be true", bRet);
	   }
	   
	   for(i=1;i<=100;i++)
	   {
	     tfsManager.rmDir(appId, userId, "/textcreateDir10w"+i);
	   }
	}
	@Ignore
	public void  test_11_createDir_deep_50()
	{
	   boolean bRet;
	   log.info( "test_11_createDir_deep_50" );
	   int i;
	   String s="/text";
	   for(i=0;i<50;i++)
	   {
	       bRet=tfsManager.createDir(appId, userId, s);
	       Assert.assertTrue("Create text should be true", bRet);
	       s="/text"+s;
	   }
	   tfsNameBaseCase BaseCase =new tfsNameBaseCase();
	   BaseCase.deleteDir("/text", 50, tfsManager);
	   tfsManager.rmDir(appId, userId, "/text");
    }
	@Ignore
	public void  test_12_createDir_width_100_deep_50()
	{
	   boolean bRet;
	   log.info( "test_12_createDir_width_100_deep_30" );
	   int i;
	   String s="/text";
	   for(i=0;i<30;i++)
	   {
	       bRet=tfsManager.createDir(appId, userId, s);
	       Assert.assertTrue("Create text should be true", bRet);
	       if(i!=29)s="/text"+s;
	   }
	   
	   for(i=1;i<=100;i++)
	   {
		   bRet=tfsManager.createDir(appId, userId, s+"/text"+i);
		   if (bRet==false)
		   {
		      System.out.println(s+"/text"+i);
		   }
		   Assert.assertTrue("Create text should be true", bRet);
	   }
	   
	   for(i=1;i<=100;i++)
	   {
		   tfsManager.rmDir(appId, userId, s+"/text"+i);
	   }
	   tfsNameBaseCase BaseCase =new tfsNameBaseCase();
	   BaseCase.deleteDir("/text", 30, tfsManager);
	   tfsManager.rmDir(appId, userId, "/text");
    }
	@Test
	public void test_13_createDir_length_more_512()
    {
 	    boolean bRet;
	    log.info( "test_13_createDir_length_more_512" );
	    int len = 512;
	 //   byte[] data = new byte[len];
	  //  Random rd = new Random();
	   // rd.nextBytes(data);
       String s = new String();
       
	   for(int i=0;i<512;i++)
       {
            int a = '0' + i%40;
               s += (char)a;
                
       }      
	    s ="/"+s;
	    bRet=tfsManager.createDir(appId, userId, s);
		Assert.assertFalse("Create Dir with more length should be false", bRet);
    }
    
	@Test
	public void test_14_createDir_more_suffix()//�кܴ������
    {
 	   boolean bRet;
	   log.info( "test_14_createDir_more_suffix" );
	   bRet=tfsManager.createDir(appId, userId, "///textcreate14ms///");
	   Assert.assertTrue("Create Dir with right path should be true", bRet);
	   tfsManager.rmDir(appId, userId, "/textcreate14ms");
    }
	@Test
	public  void test_15_createDir_empty()//�кܴ������
    {
 	   boolean bRet;
	   log.info( "test_14_createDir_more_suffix" );
	   bRet=tfsManager.createDir(appId, userId, "/    ");
	   Assert.assertFalse("Create Dir with right path should be false", bRet);
    }
      
}