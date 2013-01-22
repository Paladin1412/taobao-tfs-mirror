package com.taobao.gulu.tengine.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.gulu.database.TFS;
import com.taobao.gulu.tengine.BaseCase;
import com.taobao.gulu.tools.VerifyTool;

public class TFS_Restful_Post_File_Test_File_Size_Test extends BaseCase {

	@Test
	public void test_TFS_Restful_Post_File_Local_cache_Test_01_2kFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		
		/* put file into tfs use nginx client */
		String suffix = ".ask";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("status", "200");
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl with suffix: " + postUrl);
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");

			System.out.println("the get url is "+getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 2k�ļ���ask��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_01_2kFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".ask";
		String localFile = _2kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 100k�ļ���ico��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_02_100kFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".ico";
		String localFile = _100kFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 1M�ļ���wma��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_03_1MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".wma";
		String localFile = _1MFile;
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 14M�ļ���create��
	 * д�ļ��ɹ�������200��
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_04_14MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "$_exe";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 21 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 10M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_05_10MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "xxx.xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 10 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 9M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_06_9MFile() throws InterruptedException {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 9 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}

	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 11M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_07_11MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "xxx.xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 11 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&size=" + 11534336;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 12M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_08_12MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "$_..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 12 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 2M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_09_2MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "_..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 2 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 3M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_10_3MFile() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "$_..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 3 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 100.001M�ļ���create��
	 * д�ļ�ʧ�ܣ�����413��
	 */
	//@Test//need modify
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_11_100_001MFile() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
//		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxB01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "..jpg";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 100 * 1024 * 1024+1));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("status", "200");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
//			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
//			put2TfsKeys.add(tfsFileNameWithOutSuffix);
		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 30M�ļ���create��
	 * д�ļ�ʧ�ܣ�����200��
	 */
	//@Test//need modify
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_12_30MFile() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
//		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "..jpg";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 30 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("status", "200");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
//			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			put2TfsKeys.add(tfsFileNameWithOutSuffix);
		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 100M�ļ���create��
	 * д�ļ�ʧ�ܣ�����200��
	 */
	//@Test//need modify
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_13_100MFile() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
//		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "..jpg";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 100 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("status", "200");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
//			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			put2TfsKeys.add(tfsFileNameWithOutSuffix);
		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 1000M�ļ���create��
	 * д�ļ�ʧ�ܣ�����413��
	 */
	//@Test//need modify
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_14_1000MFile() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
//		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "..jpg";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 1000 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("status", "200");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
//			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
//			tools.showResponse(setPostMethod(postUrl, localFile));
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostMessage);
//			put2TfsKeys.add(tfsFileNameWithOutSuffix);
		

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 20M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_15_20MFile() throws InterruptedException {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "$_..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 20 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
	/* д�ļ�
	 * �ļ�����
	 * �ļ���С����
	 * 19M�ļ���create��
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����, meta��Ϣ�Ƿ���ȷ
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_File_Size_Test_16_19MFile() throws InterruptedException {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "$_..xx";
		String localFile = tmpFile;
		Assert.assertEquals("creat local temp file fail!" , 0, tfsServer.createFile(localFile, 19 * 1024 * 1024));
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			
			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
			
			/* do delete file */
			deleteFile(localFile);
		}
	}
	
}
