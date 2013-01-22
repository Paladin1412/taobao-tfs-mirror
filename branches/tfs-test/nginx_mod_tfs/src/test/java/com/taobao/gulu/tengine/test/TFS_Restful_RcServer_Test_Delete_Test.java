package com.taobao.gulu.tengine.test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.gulu.database.TFS;
import com.taobao.gulu.tengine.BaseCase;
import com.taobao.gulu.tengine.Delete;
import com.taobao.gulu.tools.VerifyTool;

public class TFS_Restful_RcServer_Test_Delete_Test extends BaseCase 
{
	@Test
	public void test_TFS_Restful_RcServer_Retry_Read2() throws InterruptedException, ExecutionException
	{
	    ExecutorService exec = Executors.newFixedThreadPool(10);   
        ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();  
        int ThreadNum = 10;
        int i;
        for (i = 0; i < ThreadNum; i++) 
        {   
            results.add(exec.submit(new Delete()));   
        }   
 
        Set  set=new HashSet();
        int n = ThreadNum;
        while(n!=0)
        {
        	TimeUnit.SECONDS.sleep(10);
 
            for(i = 0 ;i < ThreadNum; i++)
            {
                if (results.get(i).isDone()) 
                {   
                    System.out.println("!!!!!!"+results.get(i).get()+"!!!!!!"); 
                    if(set.add(i))
                        --n;
                } 
                else 
                {   
                    System.out.println("Future of "+i+" result is not yet complete");   
                }  
            }
 
            System.out.println("______________________________________");
 
        }
 
        exec.shutdown();   
    } 
	
	/* 
	 * ��������ɾ��
	 * ��T2M[T2M������������������Ⱥ�������ǰ�������ݣ�]�д��һЩT2����
	 * ͨ�������߼���Ⱥ��Appkeyȥɾ��T2���ݣ���֤�Ƿ��ܹ�������ɾ��
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_01() 
	{
		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxC01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		

		try {
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/error.log", "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/access.log", "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + NGINX.getServer_file_directory() + "logs/nginx.pid`", "", "");
			
			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage);
			
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* set post method request */
			// setting request info
			postUrl = url ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage);
		
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	

	/* 
	 * ��������ɾ��
	 * ��T2M[T2M������������������Ⱥ�������ǰ�������ݣ�]�д��һЩT2����
	 * ��ɾ��;ͨ�������߼���Ⱥ��Appkeyȥɾ��T2���ݣ�Ȼ���ٽ�������ݷ�ɾ����
	 * ���Ƿ��ܹ��ָ�������
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_02() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxC01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		String type_undelete = "2";
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		

		try {
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/error.log", "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/access.log", "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + NGINX.getServer_file_directory() + "logs/nginx.pid`", "", "");
			
			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage);
			
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* set undelete method request */
			// setting request info
			String undeleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			/* do undelete file aciton */
			tools.verifyResponse(setDeleteMethod(undeleteUrl), expectDeleteMessage);
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set post method request */
			// setting request info
			postUrl = url ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage);
		
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* set undelete method request */
			// setting request info
			undeleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			/* do undelete file aciton */
			tools.verifyResponse(setDeleteMethod(undeleteUrl), expectDeleteMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}

	/* 
	 * ��������ɾ��
	 * ��T2M[T2M������������������Ⱥ�������ǰ�������ݣ�]�д��һЩT2����
	 * ��ȡ��ͨ�������߼���Ⱥ��Appkeyȥ��ȡT2���ݣ���֤�Ƿ��ܹ������Ķ�ȡ
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_03() 
	{

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxC01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		

		try {
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/error.log", "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + NGINX.getServer_file_directory() + "logs/access.log", "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + NGINX.getServer_file_directory() + "logs/nginx.pid`", "", "");
			
			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* 
	 * ��������ɾ��
	 * ����д
	 * ����Ⱥ�д���T1Mд��T1B��������ͨappkey���߼���ȺA������
	 * ���ݻ��ȴ浽T1M����ͬ����T1B
	 * ��appkey:tfs_NginxIPMap02ɾ��������ݣ����ͽ�ԭ����T1Bɾ��
	 * ��ʵ�ʻ��T1Mɾ����ͬ��ɾ��T1B
	 * 
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_04() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		TFS tfsServer_IP = tfs_NginxIPMap02;
		String url_IP = NGINX.getRoot_url_adress();
		url_IP = url_IP + "v1/" + tfsServer_IP.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectSuccessMessage = new HashMap<String, String>();
		expectSuccessMessage.put("status", "200");
		
		Map<String, String> expectErrorMessage = new HashMap<String, String>();
		expectErrorMessage.put("status", "500");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");

		/* nginx log direct */
		String errorLog = NGINX.getServer_file_directory() + "logs/error.log";
		String accessLog = NGINX.getServer_file_directory() + "logs/access.log";
		String pid = NGINX.getServer_file_directory() + "logs/nginx.pid";
		
		
		String delete202 = "unlink, select nameserver: 10.232.36.202:5202";
		String delete209 = "unlink, select nameserver: 10.232.36.202:6209";
		String deleteT2M = "unlink, select nameserver: 10.232.36.202:7202";
		String deleteRetry = "unlink, select";
		
		String read = "read/stat";
		String write = "write";
		String unlink = "unlink";
		
		String Retry202_1 = "nameserver: 10.232.36.202:5202";
		String Retry209_1 = "nameserver: 10.232.36.209:6209";
		String RetryT2M = "nameserver: 10.232.36.202:7202";
		
		
		try {
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "0", "");
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1B�ڵ������Ƿ�ɹ�
			// setting request info
			String getUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "0", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			String deleteUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete209 + "' -c", "0", "");
			
		
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set post method request */
			// setting request info
			postUrl = url ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "0", "");

			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1B�ڵ������Ƿ�ɹ�
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* verify log info */
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + writeRetry202 + "' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "0", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			deleteUrl = url_IP + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete209 + "' -c", "0", "");
		
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
//			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
//			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
//			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* recover mysql */
			try {
				tools.verifyCMD(SERVER0435, DROPMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, CREATEMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, INITMYSQLTABLES, "", "");
				NGINX.restart();
				TimeUnit.SECONDS.sleep(15);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); Assert.assertTrue(false);
			}
		}
	}

	
	/* 
	 * ��������ɾ��
	 * ����д,T1B �� T1M���ݣ� T1M����T1B����
	 * ����Ⱥ�д���T1M����T1Bд������ͨappkey���߼���ȺA������
	 * ���ݻ��ȴ浽T1B����ͬ����T1M
	 * ��appkey:tfs_NginxIPMap01ɾ��������ݣ����T1Mɾ������ɾ��T1B������
	 * 
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_05()
	{
		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		TFS tfsServer_IP = tfs_NginxIPMap01;
		String url_IP = NGINX.getRoot_url_adress();
		url_IP = url_IP + "v1/" + tfsServer_IP.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectSuccessMessage = new HashMap<String, String>();
		expectSuccessMessage.put("status", "200");
		
		Map<String, String> expectErrorMessage = new HashMap<String, String>();
		expectErrorMessage.put("status", "500");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "200");

		/* nginx log direct */
		String errorLog = NGINX.getServer_file_directory() + "logs/error.log";
		String accessLog = NGINX.getServer_file_directory() + "logs/access.log";
		String pid = NGINX.getServer_file_directory() + "logs/nginx.pid";
		
		
		String delete202 = "unlink, select nameserver: 10.232.36.202:5202";
		String delete209 = "unlink, select nameserver: 10.232.36.209:6209";
		String deleteT2M = "unlink, select nameserver: 10.232.36.202:7202";
		String deleteRetry = "unlink, select";
		
		String read = "read/stat";
		String write = "write";
		String unlink = "unlink";
		
		String Retry202_1 = "nameserver: 10.232.36.202:5202";
		String Retry209_1 = "nameserver: 10.232.36.209:6209";
		String RetryT2M = "nameserver: 10.232.36.202:7202";

		try {
			recoverTFStoT1BBackupToT1M();
			/* modify mysql */
			tools.verifyCMD(SERVER0435, UPDATE_PHYSICS_T1B_WRITE_T1M_READ, "", "");
			

			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			NGINX.restart();
			System.out.println("Sleep 10s");
			TimeUnit.SECONDS.sleep(10);
			
			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + readRetry209 + "' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set get method request */ // ��֤����ͬ����T1M���ͽ���ȡT1M�ڵ������Ƿ�ɹ�
			// setting request info
			String getUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
		//	tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + readRetry209 + "' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");			
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			String deleteUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");//��������
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			

			/* set get method request */ // ��֤���� ������˳��  T1M���Ѿ�ɾ����T1B�л�����
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* verify log info */
			//�������֤ͬ����������
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set post method request */
			// setting request info
			postUrl = url ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + readRetry209 + "' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry209_1, "");
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1M�ڵ������Ƿ�ɹ�
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
	  //	tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + readRetry209 + "' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			deleteUrl = url_IP + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);

			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* recover mysql */
			try {
				tools.verifyCMD(SERVER0435, DROPMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, CREATEMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, INITMYSQLTABLES, "", "");
				recoverTFS();
				NGINX.restart();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); Assert.assertTrue(false);
			}
		}
	}
	
	/* 
	 * ��������ɾ��
	 * ˫��д
	 * ����Ⱥ�д���T1Mд��T1Bд����appkey��tfs_NginxIPMap01���߼���ȺA������
	 * ͨ���ͽ�����T1M��Ⱥ��appkey:tfs_NginxIPMap01ȥ�洢������,���ݻ��ȴ浽T1M����ͬ����T1B
	 * ��appkey:tfs_NginxIPMap02ɾ��������ݣ����T1Mɾ����ͬ��ɾ��T1B
	 * 
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_06() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		
		TFS tfsServer_IP01 = tfs_NginxIPMap01;
		String url_IP01 = NGINX.getRoot_url_adress();
		url_IP01 = url_IP01 + "v1/" + tfsServer_IP01.getTfs_app_key();
		
		TFS tfsServer_IP02 = tfs_NginxIPMap02;
		String url_IP02 = NGINX.getRoot_url_adress();
		url_IP02 = url_IP02 + "v1/" + tfsServer_IP02.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectSuccessMessage = new HashMap<String, String>();
		expectSuccessMessage.put("status", "200");
		
		Map<String, String> expectErrorMessage = new HashMap<String, String>();
		expectErrorMessage.put("status", "500");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		
		/* nginx log direct */
		String errorLog = NGINX.getServer_file_directory() + "logs/error.log";
		String accessLog = NGINX.getServer_file_directory() + "logs/access.log";
		String pid = NGINX.getServer_file_directory() + "logs/nginx.pid";
		
		
		String delete202 = "unlink, select nameserver: 10.232.36.202:5202";
		String delete209 = "unlink, select nameserver: 10.232.36.209:6209";
		String deleteT2M = "unlink, select nameserver: 10.232.36.202:7202";
		String deleteRetry = "unlink, select";
		
		String read = "read/stat";
		String write = "write";
		String unlink = "unlink";
		
		String Retry202_1 = "nameserver: 10.232.36.202:5202";
		String Retry209_1 = "nameserver: 10.232.36.209:6209";
		String RetryT2M = "nameserver: 10.232.36.202:7202";

		try {
			
			/* modify mysql */
			tools.verifyCMD(SERVER0435, UPDATE_PHYSICS_T1B_WRITE_T1M_WRITE, "", "");
			recoverTFStoDoubleWrite();
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			
			NGINX.restart();
			
			/* set post method request */
			// setting request info
			String postUrl = url_IP01 + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			

			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry202_1+"' -c", "1", "");
		//	tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry202_1, "");
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1B�ڵ������Ƿ�ɹ�
			// setting request info
			String getUrl = url_IP02 + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "0", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			String deleteUrl = url_IP02 + "/" + tfsFileNameWithSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP02 + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set post method request */
			// setting request info
			postUrl = url_IP01 ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry202_1+"' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	

			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1B�ڵ������Ƿ�ɹ�
			// setting request info
			getUrl = url_IP02 + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "0", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	

			
			
			/* set delete method request */ // ��֤��T1M��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			deleteUrl = url_IP02 + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete202 + "' -c", "1", "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP02 + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* recover mysql */
			try {
				recoverTFS();
				tools.verifyCMD(SERVER0435, DROPMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, CREATEMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, INITMYSQLTABLES, "", "");
				NGINX.restart();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); Assert.assertTrue(false);
			}
		}
	}
	
	/* 
	 * ��������ɾ��
	 * ˫��д
	 * ����Ⱥ�д���T1Mд��T1Bд����appkey��tfs_NginxIPMap02���߼���ȺA������
	 * ͨ���ͽ�����T1B��Ⱥ��appkey:tfs_NginxIPMap02ȥ�洢������,���ݻ��ȴ浽T1B����ͬ����T1M
	 * ��appkey:tfs_NginxIPMap01ɾ��������ݣ����T1Bɾ����ͬ��ɾ��T1M
	 * 
	 */
	@Test
	public void test_TFS_Restful_RcServer_Test_Delete_Test_07() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		
		TFS tfsServer_IP01 = tfs_NginxIPMap01;
		String url_IP01 = NGINX.getRoot_url_adress();
		url_IP01 = url_IP01 + "v1/" + tfsServer_IP01.getTfs_app_key();
		
		TFS tfsServer_IP02 = tfs_NginxIPMap02;
		String url_IP02 = NGINX.getRoot_url_adress();
		url_IP02 = url_IP02 + "v1/" + tfsServer_IP02.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".2k";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectSuccessMessage = new HashMap<String, String>();
		expectSuccessMessage.put("status", "200");
		
		Map<String, String> expectErrorMessage = new HashMap<String, String>();
		expectErrorMessage.put("status", "500");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		
		/* nginx log direct */
		String errorLog = NGINX.getServer_file_directory() + "logs/error.log";
		String accessLog = NGINX.getServer_file_directory() + "logs/access.log";
		String pid = NGINX.getServer_file_directory() + "logs/nginx.pid";
		
		
		String delete202 = "unlink, select nameserver: 10.232.36.202:5202";
		String delete209 = "unlink, select nameserver: 10.232.36.209:6209";
		String deleteT2M = "unlink, select nameserver: 10.232.36.202:7202";
		String deleteRetry = "unlink, select";
		
		String read = "read/stat";
		String write = "write";
		String unlink = "unlink";
		
		String Retry202_1 = "nameserver: 10.232.36.202:5202";
		String Retry209_1 = "nameserver: 10.232.36.209:6209";
		String RetryT2M = "nameserver: 10.232.36.202:7202";
		

		try {
			
			/* modify mysql */
			tools.verifyCMD(SERVER0435, UPDATE_PHYSICS_T1B_WRITE_T1M_WRITE, "", "");
			recoverTFStoDoubleWrite();
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			NGINX.restart();
			
			/* set post method request */
			// setting request info
			String postUrl = url_IP02 + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "1", "");
	//		tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	

			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1M�ڵ������Ƿ�ɹ�
			// setting request info
			String getUrl = url_IP01 + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set delete method request */ // ��֤��T1B��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			String deleteUrl = url_IP01 + "/" + tfsFileNameWithSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			
			/* verify log info */
//			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + writeRetry202 + "' -c", "1", "");
//			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + writeRetry + "'", Retry202_1, "");
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete209 + "' -c", "1", "");
			
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP01 + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set post method request */
			// setting request info
			postUrl = url_IP02 ;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + write + "'|grep '"+Retry209_1+"' -c", "1", "");
		//	tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + write + "'", Retry209_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	

			
			/* set get method request */ // ��֤����ͬ�����ͽ���ȡT1M�ڵ������Ƿ�ɹ�
			// setting request info
			getUrl = url_IP01 + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "0", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set delete method request */ // ��֤��T1B��ɾ�����ݣ���ɾ���ɹ�
			// setting request info
			deleteUrl = url_IP01 + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectSuccessMessage);
			// ��֤ɾ����ɾ��˳��
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + delete209 + "' -c", "1", "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
			/* set get method request */ // ��֤���� ������˳��
			// setting request info
			getUrl = url_IP01 + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessageAfterDelete);
			/* verify log info */
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry202_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep '" + read + "'|grep '"+Retry209_1+"' -c", "1", "");
			tools.verifyCMD(SERVER0435, "cat " + errorLog + "| grep -m 1 '" + read + "'", Retry202_1, "");
			/* clean the nginx log */
			tools.verifyCMD(SERVER0435, "rm -fr " + errorLog, "", "");
			tools.verifyCMD(SERVER0435, "rm -fr " + accessLog, "", "");
			tools.verifyCMD(SERVER0435, "kill -USR1 `cat " + pid + "`", "", "");	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* recover mysql */
			try
			{
				recoverTFS();
				tools.verifyCMD(SERVER0435, DROPMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, CREATEMYSQLTABLES, "", "");
				tools.verifyCMD(SERVER0435, INITMYSQLTABLES, "", "");
				NGINX.restart();
				TimeUnit.SECONDS.sleep(15);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); Assert.assertTrue(false);
			}
		}
	}
	
}
