package com.taobao.gulu.tengine.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.gulu.database.TFS;
import com.taobao.gulu.tengine.BaseCase;
import com.taobao.gulu.tools.VerifyTool;

public class TFS_Restful_Post_File_Test_URL_Arg_Test extends BaseCase {

	/*
	 * д�ļ�
	 * url��������
	 * appkey���� 
	 * appkey������ 
	 * д�ļ�ʧ�ܣ�����500��
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_01_appkey_not_exit() {

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
		String suffix = ".bmp";
		String localFile = bmpFile;

		/* set expect response message */
		Map<String, String> expectPostErrorMessage = new HashMap<String, String>();
		expectPostErrorMessage.put("status", "500");

		try {

			/* set post method request */
			// setting request info
			String postUrl = url + "*" + "?suffix=" + suffix;
			System.out.println("the postUrl with suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);
		

			/* set post method request */
			// setting request info
			postUrl = url + "1";
			System.out.println("the postUrl without suffix: " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊbmp��suffixΪbmpp
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_02_suffix_bmp() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmpp";
		String localFile = bmpFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊswf��suffixΪswf.
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_03_suffix_swf() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA02;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "swf.";
		String localFile = swfFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊcsw��suffixΪswf
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_04_suffix_csw() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".gif";
		String localFile = cswFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {					
					tfsServer.delete(key, null);
					System.out.println("tfsFileName for delete is " + key);
				}
			}
			put2TfsKeys.clear();
		}
	}

	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊgif��suffixΪ*if1
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_05_suffix_gif() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".*if1";
		String localFile = gifFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			/* do delete tfs file */
			if (put2TfsKeys.size() > 0) {
				for (String key : put2TfsKeys) {
					System.out.println("tfsFileName for delete is " + key);
					tfsServer.delete(key, suffix);
				}
			}
			put2TfsKeys.clear();
		}
	}
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊtxt��suffixΪt*t
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_06_suffix_txt() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".t*t";
		String localFile = txtFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊico��suffixΪic
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_07_suffix_ico() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "ic";
		String localFile = icoFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊjpg��suffixΪjpeg
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_08_suffix_jpg() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".jpeg";
		String localFile = jpgFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊpng��suffixΪpng*
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_09_suffix_png() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".png*";
		String localFile = png10kFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊzip��suffixΪrar
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_10_suffix_zip() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".rar";
		String localFile = zipFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊrar��suffixΪzip
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_11_suffix_rar() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".zip";
		String localFile = rarFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * suffix���� 
	 * suffix����д�ļ����Ͳ�һ��
	 * ��д�ļ�Ϊjpg��suffixΪ�����ַ�
	 * д�ļ��ɹ�������200����ȡ���ļ��ж��ļ��Ƿ�����
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_12_suffix_special() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = "T...jpg...xx";
		String specialSuffix = ".jpg";
		String localFile = jpgFile;
		
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
			System.out.println("the postUrl : " + postUrl);
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
			Map<String, String> expectGetMetaMessageWithOutSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithOutSuffix.put("body", tfsFileMetaWithSuffix);
			expectGetMetaMessageWithOutSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithOutSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl with suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithOutSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + suffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get meta info request */
			// setting request info -- with special suffix
			getMetaUrl = url + "/metadata/" + tfsFileNameWithSuffix + "?suffix=" + specialSuffix;
			System.out.println("the getUrl with special suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponse(setGetMethod(getMetaUrl), expectGetErrorMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithSuffix + "?suffix=" + specialSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ0
	 * ����suffix�� �൱��д���һ���ļ�
	 * ����һ���ļ���
	 *	��ȡ���ļ����ļ�������Ϻ�׺
	 *		������ȷ��������ļ�
	 *	��ȡ���ļ����ļ�����û�м��Ϻ�׺
	 *		������ȷ��������ļ�
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_13_simple_name_0_suffix() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "0";
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ0
	 * ������suffix
	 * ����һ���ļ���
	 *	��ȡ���ļ����ļ�����û�м��Ϻ�׺
	 *	 	������ȷ��������ļ�
	 *	��ȡ���ļ����ļ�������Ϻ�׺
	 *		������ȷ��������ļ�
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_14_simple_name_0_no_suffix() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String noSuffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "0";
		
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
			String postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + noSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ1
	 * ����suffix�� �൱��д��ڶ����ļ�
	 * ����һ���ļ���
	 *	��ȡ���ļ����ļ�������Ϻ�׺
	 *		������ȷ��������ļ�
	 *	��ȡ���ļ����ļ�����û�м��Ϻ�׺
	 *		������ȷ��������ļ�
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_15_simple_name_1_suffix() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithSuffix = null;
		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "1";
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
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
			getUrl = url + "/" + splitString(tfsFileNameWithSuffix) ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ1
	 * ������suffix
	 * ����һ���ļ���
	 *	��ȡ���ļ����ļ�����û�м��Ϻ�׺
	 *	 	������ȷ��������ļ�
	 *	��ȡ���ļ����ļ�������Ϻ�׺
	 *		������ȷ��������ļ�
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_16_simple_name_1_no_suffix() {

		VerifyTool tools = new VerifyTool();
		String tfsFileNameWithOutSuffix = null;
		String tfsFileMetaWithOutSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String noSuffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "1";
		
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
			String postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tfsFileNameWithOutSuffix = tools.verifyResponseAndGetTFSFileName(setPostMethod(postUrl, localFile), expectPostMessage);
			System.out.println("the tfs file name with suffix is  : " + tfsFileNameWithOutSuffix);
			put2TfsKeys.add(tfsFileNameWithOutSuffix);
			
			/* set get meta info request */
			// use java client get the meta info
			tfsFileMetaWithOutSuffix = tools.converttfsFileNametoJson(tfsServer, tfsFileNameWithOutSuffix);
			System.out.print("tfs file meta with suffix is : ");
			System.out.println(tfsFileMetaWithOutSuffix);
			// set expect response message
			Map<String, String> expectGetMetaMessageWithSuffix = new HashMap<String, String>();
			expectGetMetaMessageWithSuffix.put("body", tfsFileMetaWithOutSuffix);
			expectGetMetaMessageWithSuffix.put("Content-Type", "application/json");
			expectGetMetaMessageWithSuffix.put("status", "200");
			// setting request info -- with suffix
			String getMetaUrl = url + "/metadata/" + tfsFileNameWithOutSuffix ;
			System.out.println("the getUrl without suffix is : " + getMetaUrl);
			/* do get file action */
			//tools.showResponse(setGetMethod(getUrl));
			tools.verifyResponseWithJSON(setGetMethod(getMetaUrl), expectGetMetaMessageWithSuffix);
			
			/* set get method request */
			// setting request info
			String getUrl = url + "/" + tfsFileNameWithOutSuffix;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponseBodyWithLocalFile(setGetMethod(getUrl), expectGetMessage);
			
			/* set get method request */
			// setting request info
			getUrl = url + "/" + tfsFileNameWithOutSuffix + "?suffix=" + noSuffix ;
			System.out.println("the getUrl : " + getUrl);

			/* do get file action */
			tools.verifyResponse(setGetMethod(getUrl), expectGetErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ2
	 * ����500
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_17_simple_name_2() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "2";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectPostErrorMessage = new HashMap<String, String>();
		expectPostErrorMessage.put("status", "400");
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);
			
			/* set post method request */
			// setting request info
			postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ3
	 * ����500
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_18_simple_name_3() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "3";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectPostErrorMessage = new HashMap<String, String>();
		expectPostErrorMessage.put("status", "400");
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);
			
			/* set post method request */
			// setting request info
			postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ01
	 * ����500
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_19_simple_name_01() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "01";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectPostErrorMessage = new HashMap<String, String>();
		expectPostErrorMessage.put("status", "400");
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);
			
			/* set post method request */
			// setting request info
			postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	/*
	 * д�ļ�
	 * url��������
	 * simple_name���� 
	 * simple_nameֵΪ00
	 * ����500
	 */
	@Test
	public void test_TFS_Restful_Post_File_Test_URL_Arg_Test_20_simple_name_00() {

		VerifyTool tools = new VerifyTool();
//		String tfsFileNameWithSuffix = null;
//		String tfsFileMetaWithSuffix = null;

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		url = url + "v1/" + tfsServer.getTfs_app_key();
		
		/* put file into tfs use nginx client */
		String suffix = ".bmp";
		String localFile = bmpFile;
		String simple_name = "00";
		
		/* set expect response message */
		Map<String, String> expectPostMessage = new HashMap<String, String>();
		expectPostMessage.put("Content-Type", "application/json");
		
		Map<String, String> expectPostErrorMessage = new HashMap<String, String>();
		expectPostErrorMessage.put("status", "400");
		
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
			String postUrl = url + "?suffix=" + suffix + "&simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);
			
			/* set post method request */
			// setting request info
			postUrl = url + "?simple_name=" + simple_name;
			System.out.println("the postUrl : " + postUrl);
			// set the post method
			/* do post file action */
			tools.verifyResponse(setPostMethod(postUrl, localFile), expectPostErrorMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
