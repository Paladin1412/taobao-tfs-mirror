package com.taobao.common.tfs.performance;


import java.util.List;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.taobao.common.tfs.namemeta.FileMetaInfo;


public class MutiThreadTest extends tfsNameBaseCase {
	public  static FileMetaInfo       file_info =null; 
	public  static List<FileMetaInfo>  file_info_List = null;
    public  static String file_path = "";
    public  static String cur_level = "";
	@Before
	public void setUp() throws Exception 
	{
		 tfsManager.createDir(userId, "/public");
	}

	@After
	public void tearDown() throws Exception 
	{}
	public void DirRecurCreate(int count,String current_level,int level_count,int dir_num_per_level)
	{
		if(count < level_count)
		{   
		    boolean ret ;
     		for( int i= 1;i<=dir_num_per_level;i++ )
     		{
		       ret  = tfsManager.createDir(userId, current_level+"/"+String.valueOf(i));
     	       Assert.assertTrue(ret);
     		   log.info("now create path : "+current_level+"/"+String.valueOf(i));
     		   /*for(int j = 1; j <= 5;j++)
     		   {
     			  ret = tfsManager.createFile(current_level+"/"+String.valueOf(i)+"/"+String.valueOf(j+4));
        	      Assert.assertTrue(ret);
     		   }*/
     		  DirRecurCreate(count+1,current_level+"/"+String.valueOf(i),level_count,dir_num_per_level);
		    }

		}
		return;
	}
	public void DirRecurRemove(String current_level)
	{
  	 
		List<FileMetaInfo> file_info_List =  tfsManager.lsDir(userId, current_level); 
	    int num = file_info_List.size();
	    if(num == 0)
	    {
	    	if(current_level=="/public")
	    	{
	    		return;
	    	}
	    	else
	    	{
	    		log.info("��ǰĿ¼��"+current_level+"Ϊ��");
	    		boolean ret = tfsManager.rmDir(userId, current_level);
  	            Assert.assertTrue(ret);
  	    		log.info("��ɾ����"+current_level);
  	            return;
	    	}
	    }
	    else
	    {
    		log.info("��ǰĿ¼��"+current_level+"��"+num+"����Ŀ¼");
    		FileMetaInfo file_info = null;
	        for(int i=0;i<num;i++)
	        {
	        	file_info = file_info_List.get(i);
	 	        DirRecurRemove(current_level+"/"+file_info.getFileName());
	    		log.info("�ݹ�ɾ��"+current_level+"/"+file_info.getFileName()+"�µ�Ŀ¼");
	 	        
	        }
    		boolean ret = tfsManager.rmDir(userId, current_level);
	        Assert.assertTrue(ret);
	    }

	}
	
	@Test
	public void test01_createDir()
	{
		DirRecurCreate(0,"/public",7,4);
		DirRecurRemove("/public");
   	}
	

}
