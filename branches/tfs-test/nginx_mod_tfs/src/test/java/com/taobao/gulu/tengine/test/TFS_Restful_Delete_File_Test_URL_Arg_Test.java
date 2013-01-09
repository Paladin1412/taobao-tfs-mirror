package com.taobao.gulu.tengine.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.taobao.gulu.database.TFS;
import com.taobao.gulu.tengine.BaseCase;
import com.taobao.gulu.tools.VerifyTool;

public class TFS_Restful_Delete_File_Test_URL_Arg_Test extends BaseCase {
	
	@After
	public void sleep() throws InterruptedException{
		TimeUnit.SECONDS.sleep(10);
		System.out.println("sleep 10s in after");
	}
	

	/* ɾ���ļ�
	 * url��������
	 * Appkey����
	 * appkey��������filename����
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_01_appkey() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileMetaWithOutSuffix = null;
		

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		TFS tfsServer_noFile = tfs_NginxB01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		String url_delete = NGINX.getRoot_url_adress();
		url_delete = url_delete + "v1/" + tfsServer_noFile.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 1;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "404");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url_delete + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			

			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithSuffix, suffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithSuffix);
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
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
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			deleteUrl = url_delete + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			

			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta without suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessageAfterDelete);
			
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

	/* ɾ���ļ�
	 * url��������
	 * FileName����
	 * filename������
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_02_fileName_noExit() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameNotExit = "Tse4$3*-xxxesdxdsf";
//		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* set expect response message */
		Map<String, String> expectDeleteMessageNotExit = new HashMap<String, String>();
		expectDeleteMessageNotExit.put("status", "404");
		

		try {
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameNotExit;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessageNotExit);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * FileName����
	 * filename�����Ҳ���ȷ
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_03_fileName_Exit_noCorrect() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".csw";
		String localFile = cswFile;
//		int expectStatu = 1;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessageNotCorrect = new HashMap<String, String>();
		expectDeleteMessageNotCorrect.put("status", "404");
		
		Map<String, String> expectGetMessageAfterDelete = new HashMap<String, String>();
		expectGetMessageAfterDelete.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "noCorrect";
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessageNotCorrect);
			

			/* set get meta info request */
			// set expect response message
			// setting request info -- with suffix
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊbmp��δ����suffix��ɾ���ļ���suffixΪbmp
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_04_suffix_delete_noSuffix_bmp() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊswf��δ����suffix��ɾ���ļ���suffixΪswf
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_05_suffix_delete_noSuffix_swf() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".swf";
		String localFile = swfFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊcsw��δ����suffix��ɾ���ļ���suffixΪcsw
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_06_suffix_delete_noSuffix_csw() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".csw";
		String localFile = cswFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊgif��δ����suffix��ɾ���ļ���suffixΪgif
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_07_suffix_delete_noSuffix_gif() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".gif";
		String localFile = gifFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊtxt��δ����suffix��ɾ���ļ���suffixΪtxt
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_08_suffix_delete_noSuffix_txt() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".txt";
		String localFile = txtFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊico��δ����suffix��ɾ���ļ���suffixΪico
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_09_suffix_delete_noSuffix_ico() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".ico";
		String localFile = icoFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊjpg��δ����suffix��ɾ���ļ���suffixΪjpg
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_10_suffix_delete_noSuffix_jpg() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".jpg";
		String localFile = jpgFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊpng��δ����suffix��ɾ���ļ���suffixΪpng
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_11_suffix_delete_noSuffix_png() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".png";
		String localFile = png10kFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊzip��δ����suffix��ɾ���ļ���suffixΪzip
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_12_suffix_delete_noSuffix_zip() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".zip";
		String localFile = zipFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix����д�ļ����Ͳ�һ�£�δ����suffix��
	 * ��д�ļ�Ϊrar��δ����suffix��ɾ���ļ���suffixΪrar
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_13_suffix_delete_noSuffix_rar() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".rar";
		String localFile = rarFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);

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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + suffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
//			tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊbmp��suffixΪbmpp
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_14_suffix_delete_withSuffix_bmpp() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String wrongSuffix = ".bmpp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊswf��suffixΪswf.
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_15_suffix_delete_withSuffix_swf() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".swf";
		String wrongSuffix = ".swf.";
		String localFile = swfFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊgif��suffixΪ*if
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_16_suffix_delete_withSuffix_gif() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".gif";
		String wrongSuffix = ".*if";
		String localFile = gifFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊtxt��suffixΪt*t
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_17_suffix_delete_withSuffix_txt() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".txt";
		String wrongSuffix = ".t*t";
		String localFile = txtFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊico��suffixΪic
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_18_suffix_delete_withSuffix_ico() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".ico";
		String wrongSuffix = ".ic";
		String localFile = icoFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊjpg��suffixΪjpeg
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_19_suffix_delete_withSuffix_jpg() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".jpg";
		String wrongSuffix = "jpeg";
		String localFile = jpgFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊpng��suffixΪpngpng
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_20_suffix_delete_withSuffix_png() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".png";
		String wrongSuffix = ".pngpng";
		String localFile = png10kFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊzip��suffixΪrar
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_21_suffix_delete_withSuffix_zip() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".zip";
		String wrongSuffix = ".rar";
		String localFile = zipFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * Suffix����
	 * suffix��д�ļ�ʱ������suffix���Ͳ�һ��
	 * ��д�ļ�������suffix����Ϊrar��suffixΪzip
	 * ����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_22_suffix_delete_withSuffix_rar() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".rar";
		String wrongSuffix = ".zip";
		String localFile = rarFile;
		int expectStatu = 0;
//		String type = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);

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
			
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + wrongSuffix;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
			// setting request info -- with suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ0��ɾ���ļ�
	 * file�����ѱ�ɾ��
	 * ɾ���ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_23_type_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 1;
		String type_delete = "0";
		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

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
			
			/* delete tfs file use java client */
			// do delete tfs file
			tfsServer.delete(tfsFileNameWithSuffix, suffix);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			 postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* delete tfs file use java client */
			// do delete tfs file
			tfsServer.delete(tfsFileNameWithOutSuffix);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix + "?type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ0��ɾ���ļ�
	 * file��������
	 * ɾ���ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_24_type_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String type_delete = "0";
		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix + "?type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ0��ɾ���ļ�
	 * ɾ��һ�����ڵ������ļ�
	 * ɾ���ɹ�
	 * �Ѹ��ļ������أ�������ʧ��
	 * �ٴ�ȥɾ��
	 * ɾ��ʧ��
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_25_type_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu_hide = 4;
		int expectStatu = 0;
//		int expectStatu_delete = 1;
		int expectStatu_hide_delete = 1+4;
		String type_delete = "0";
		String type_hide = "4";
		String type_unhide = "6";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		

		try {

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
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* set delete method request */
			// setting request info to hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			
			// setting request info to delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide_delete);
			
			
			// setting request info to unhide the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide_delete);
			
			
			// setting request info to unhide the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide_delete);
			
			

			/* set post method request */
			// setting request info
			 postUrl = url;
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
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* set delete method request */
			// setting request info to hide the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			
			// setting request info to delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide_delete);
			
			
			// setting request info to unhide the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide_delete);
			
			
			// setting request info to unhide the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide_delete);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ2����ɾ���ļ�
	 * file����δ��ɾ��
	 * ��ɾ���ļ�ʧ�ܣ�����404����֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_26_type_2() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		String type_delete = "0";
		String type_undelete = "2";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ2����ɾ���ļ�
	 * file����ɾ��
	 * ��ɾ���ļ��ɹ�������200����֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_27_type_2() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		int expectStatu_delete = 1;
//		int expectStatu_hide = 4;
		String type_delete = "0";
		String type_undelete = "2";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ2����ɾ���ļ�
	 * file��������
	 * ɾ���ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_28_type_2() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String type_undelete = "2";
		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			

			/* set get meta info request after delete */
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
			getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix + "?type=" + type_meta;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ2����ɾ���ļ�
	 * ��ɾ��һ�������ļ�
	 * ��ɾ���ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_29_type_2() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
//		int expectStatu_delete_hide = 1+4;
		int expectStatu_hide = 4;
//		int expectStatu_unhide = 6;
		String type_hide = "4";
		String type_undelete = "2";
//		String type_delete = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_undelete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ4�������ļ�
	 * file����δ����
	 * �����ļ��ɹ�������200����֤���ļ��Ƿ��������ļ������Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_30_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_hide = 4;
//		String type_delete = "0";
		String type_hide = "4";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp"	);
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ4�������ļ�
	 * file����������
	 * �����ļ�ʧ�ܣ�����404�� ��֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_31_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
//		int expectStatu_delete = 1;
		int expectStatu_hide = 4;
		String type_hide = "4";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ4�������ļ�
	 * file��������
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_32_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String type_hide = "4";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ4�������ļ�
	 * ����һ����ɾ�����ļ�
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_33_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_delete = 1;
//		int expectStatu_hide = 4;
//		int expectStatu_unhide = 6;
		String type_hide = "4";
//		String type_undelete = "2";
		String type_delete = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			TimeUnit.SECONDS.sleep(20);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ6���������ļ�
	 * file����δ����
	 * �������ļ�ʧ�ܣ�����404 �� ��֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_34_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_unhide = 6;
//		String type_delete = "0";
		String type_unhide = "6";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp"	);
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ6���������ļ�
	 * file����������
	 * �������ļ��ɹ�������200����֤���ļ��Ƿ񱻷��������ļ������Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_35_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_delete = 1;
		int expectStatu_hide = 4;
		String type_hide = "4";
		String type_unhide = "6";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ6���������ļ�
	 * file��������
	 * �������ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_36_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String type_unhide = "6";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ6���������ļ�
	 * ������һ����ɾ�����ļ�
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_37_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_delete = 1;
//		int expectStatu_hide = 4;
//		int expectStatu_unhide = 6;
		String type_unhide = "6";
		String type_delete = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ3
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_38_type_3() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "3";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ01
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_39_type_01() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "01";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ11
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_40_type_11() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "11";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ1������
	 * file����δ����
	 * �����ļ��ɹ�������200����֤���ļ��Ƿ��������ļ������Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_41_hide_1() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_hide = 4;
//		String type_delete = "0";
		String hide = "1";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp"	);
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			TimeUnit.SECONDS.sleep(20);
			
			/* do get file action */
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ1������
	 * file����������
	 * �����ļ�ʧ�ܣ�����404�� ��֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_42_hide_1() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
//		int expectStatu_delete = 1;
		int expectStatu_hide = 4;
		String hide = "1";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ1������
	 * file��������
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_43_hide_1() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String hide = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ1������
	 * ����һ����ɾ�����ļ�
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_44_hide_1() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_delete = 1;
//		int expectStatu_hide = 4;
//		int expectStatu_unhide = 6;
		String hide = "1";
//		String type_undelete = "2";
		String type_delete = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			TimeUnit.SECONDS.sleep(20);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ0��������
	 * file����δ����
	 * �������ļ�ʧ�ܣ�����404 �� ��֤���ļ������Ƿ�������Modifyͷ�Ƿ�δ�仯
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_45_hide_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_unhide = 6;
//		String type_delete = "0";
		String unhide = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp"	);
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ0��������
	 * file����������
	 * �������ļ��ɹ�������200����֤���ļ��Ƿ񱻷��������ļ������Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_46_hide_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_delete = 1;
		int expectStatu_hide = 4;
		String type_hide = "4";
		String unhide = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			TimeUnit.SECONDS.sleep(20);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ0��������
	 * file��������
	 * �������ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_47_hide_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;
//		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String fileNotExit = "noExit";
		int expectStatu = 0;
		String unhide = "0";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + fileNotExit + "?hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + fileNotExit + "?hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ0��������
	 * ������һ����ɾ�����ļ�
	 * �����ļ�ʧ�ܣ�����404
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_48_hide_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_delete = 1;
//		int expectStatu_hide = 4;
//		int expectStatu_unhide = 6;
		String unhide = "0";
		String type_delete = "0";
//		String type_meta = "1";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "404");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info hide the tfs file
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info delete the hided tfs file
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix + "&hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?&type=" + type_delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info undelete the tfs file
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
//			tools.showResponse(setDeleteMethod(deleteUrl));
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			System.out.println("the getUrl : " + getUrl);
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ00
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_49_hide_00() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "00";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ11
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_50_hide_11() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "11";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ3
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_51_hide_3() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "3";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ22
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_52_hide_22() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "22";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide������һ�µ����
	 * ��Ϊ����
	 * �������سɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_53_hide_1_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_hide = 4;
		String hide = "1";
		String type = "4";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
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
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide������һ�µ����
	 * ��Ϊ������
	 * ���ط����سɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_54_hide_0_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		int expectStatu_hide = 4;
		String hide = "1";
		String unhide = "0";
		String type = "6";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + unhide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + unhide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪ���أ�hideΪ������
	 * �������سɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_55_hide_0_type_4() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
		int expectStatu_hide = 4;
		String hide = "0";
		String type = "4";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪ�����أ�hideΪ����
	 * ���ط����سɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_56_hide_1_type_6() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		int expectStatu_hide = 4;
		String hide = "1";
//		String unhide = "2";
		String type = "6";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_hide);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪɾ����hideΪ����
	 * ����ɾ���ɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_57_hide_1_type_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
//		int expectStatu_hide = 4;
		int expectStatu_delete = 1;
		String hide = "1";
		String type = "0";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪɾ����hideΪ������
	 * ����ɾ���ɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_58_hide_0_type_0() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
//		int expectStatu = 0;
//		int expectStatu_hide = 4;
		int expectStatu_delete = 1;
		String hide = "0";
		String type = "0";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
//			/* do delete tfs file */
//			if (put2TfsKeys.size() > 0) {
//				for (String key : put2TfsKeys) {
//					System.out.println("tfsFileName for delete is " + key);
//					tfsServer.delete(key, null);
//				}
//			}
//			put2TfsKeys.clear();
		}
	}
	
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪ��ɾ����hideΪ����
	 * ���ط�ɾ���سɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_59_hide_1_type_2() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_hide = 4;
		int expectStatu_delete = 1;
		String delete = "0";
		String hide = "1";
		String type = "2";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type��hideͬʱ����
	 * type��hide�����ò�һ�µ����
	 * typeΪ��ɾ����hideΪ������
	 * ���ط�ɾ���ɹ���200
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_60_hide_0_type_2() throws InterruptedException {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA03;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
//		int expectStatu_hide = 4;
		int expectStatu_delete = 1;
		String delete = "0";
		String hide = "0";
		String type = "2";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		
		Map<String, String> expectDeleteMessage = new HashMap<String, String>();
		expectDeleteMessage.put("status", "200");
		
		Map<String, String> expectGetErrorMessage = new HashMap<String, String>();
		expectGetErrorMessage.put("status", "404");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide + "&type=" + type;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + delete;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu_delete);
			
			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);
			
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type + "&hide=" + hide;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteMessage); TimeUnit.SECONDS.sleep(20);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}finally{
			/* do delete tfs file */
			TimeUnit.SECONDS.sleep(20);
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, null);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ01
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_61_hide_01() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "01";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * hide����
	 * ֵΪ02
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_62_hide_02() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String hide_special = "02";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?hide=" + hide_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ00
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_63_type_00() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "00";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ02
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_64_type_02() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "02";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ04
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_65_type_04() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "04";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
	/* ɾ���ļ�
	 * url��������
	 * type����
	 * ֵΪ06
	 * ����Bad Request
	 */
	@Test
	public void test_TFS_Restful_Delete_File_Test_URL_Arg_Test_66_type_06() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileNameWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		int expectStatu = 0;
		String type_special = "01";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectGetMessage = new HashMap<String, String>();
		expectGetMessage.put("body", localFile);
		/* ---------------------------------------------- */
		//expectGetMessage.put("Content-Type", "image/x-ms-bmp");
		/* ---------------------------------------------- */
		
		Map<String, String> expectDeleteErrorMessage = new HashMap<String, String>();
		expectDeleteErrorMessage.put("status", "400");
		

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "?suffix=" + suffix;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithSuffix);
			put2TfsKeys.add(tfsFileNameWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			String deleteUrl = url + "/" + tfsFileNameWithSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithSuffix, suffix, expectStatu);
			
			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			

			/* set post method request */
			// setting request info
			postUrl = url;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name without suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set delete method request */
			// setting request info
			deleteUrl = url + "/" + tfsFileNameWithOutSuffix + "?type=" + type_special;
			System.out.println("the deleteUrl : " + deleteUrl);
			
			/* do delete file aciton */
			tools.verifyResponse(setDeleteMethod(deleteUrl), expectDeleteErrorMessage);
			
			// use java client verify the status info 
			tools.verifyTFSFileStatu(tfsServer, tfsFileNameWithOutSuffix, expectStatu);
			
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
	
}
