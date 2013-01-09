package com.taobao.gulu.tengine.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import org.json.*;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Assert;
import org.junit.Test;

import com.taobao.common.tfs.namemeta.FileMetaInfo;
import com.taobao.common.tfs.unique.UniqueStore;
import com.taobao.common.tfs.unique.UniqueValue;
import com.taobao.gulu.database.TFS;
import com.taobao.gulu.database.Tair;
import com.taobao.gulu.tengine.BaseCase;
import com.taobao.gulu.tools.VerifyTool;

/* �Զ����ļ�������
 * 
 * 
 */
public class TFS_Restful_UserDefinedDir_Test extends BaseCase {
	/*
	 * 
	 * 
	 * Ŀ¼ ��creat delete get ls mv��
	 */

	/*
	 * create dir ����201
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateDirReturn201Test() 
	{

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_201create201";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);

			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * create dir ����401 appid����appkey��Ӧ��appid
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateDirReturn401Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/2" + "/1"
				+ "/dir" + "/dir_401";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "401");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * create dir ����400 appid���Ϸ�
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateDirReturn400Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/X" + "/1"
				+ "/dir" + "/dir_400";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "400");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * create dir ����404 ��Ŀ¼������
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateDirReturn404Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_404/dir_404/";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "404");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * create dir ����403 ���������Ŀ¼�����ļ�����Ŀ¼���
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateDirReturn403Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url
				+ "v2/"
				+ tfsServer.getTfs_app_key()
				+ "/1"
				+ "/1"
				+ "/dir"
				+ "/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403/dir_403";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "403");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * delete dir ����400 Ŀ¼/�ļ������Ϲ淶
	 */
	@Test
	public void test_TFS_RestfulUserDefinedDeleteDirReturn400Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/X" + "/1"
				+ "/dir" + "/dir_201";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "400");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");

			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * delete dir ����401 ��Ȩ�ޣ�Ŀǰÿ��Ӧ��ֻ���Լ���appid�µĿռ�ӵ���޸�Ȩ�ޣ���������/ɾ��/�ƶ�Ŀ¼/�ļ���д�ļ���
	 */
	@Test
	public void test_TFS_RestfulUserDefinedDeleteDirReturn401Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/2" + "/1"
				+ "/dir" + "/dir_201";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "401");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");

			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * delete dir ����403Ŀ¼�ǿ� Ŀ¼����Ŀ¼
	 */
	@Test
	public void test_TFS_RestfulUserDefinedDeleteDirReturn403ExistDirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_403/dir_403";
		String urlMetaParent = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + "/dir_403";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "403");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			// create dir_403/dir_403
			tools.verifyResponse(setPostMethod(urlMetaParent),
					expectPostMessage);
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);
			tools.verifyResponse(setDeleteMethod(urlMetaParent),
					expectDeleteMessage);

			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlMetaParent),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * delete dir ����403Ŀ¼�ǿ� Ŀ¼�����ļ�
	 */
	@Test
	public void test_TFS_RestfulUserDefinedDeleteDirReturn403ExistFileTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlFile = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/file/dir_403/localFile";
		String urlMetaParent = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir/dir_403";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "403");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			System.out.println("the postUrl   is : " + urlFile);
			/* do post file action */
			System.out.println("creat dir  begin");
			// create dir_403/dir_403
			tools.verifyResponse(setPostMethod(urlMetaParent),
					expectPostMessage);
			tools.verifyResponse(setPostMethod(urlFile + "?recursive=0"),
					expectPostMessage);
			// ɾ�� ����403 �������ļ�δɾ��
			tools.verifyResponse(setDeleteMethod(urlMetaParent),
					expectDeleteMessage);

			// ɾ���ļ�����ɾ��Ŀ¼�ɹ�
			tools.verifyResponse(setDeleteMethod(urlFile),
					expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlMetaParent),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * delete dir ����404 Ŀ¼���߸�Ŀ¼������
	 */
	@Test
	public void test_TFS_RestfulUserDefinedDeleteDirReturn404Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_404/dir_404";
		String urlMetaParent = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + "/dir_404";
		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "404");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			String getMetaUrl = urlMeta;
			System.out.println("the getUrl   is : " + getMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");

			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);

			tools.verifyResponse(setDeleteMethod(urlMetaParent),
					expectDeleteMessage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * mv dir ����200 �����ɹ�
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn200Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_srcmv200";
		String destDir = "/dir_destmv200";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir + "?recursive=0";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage404 = new HashMap<String, String>();
			expectDeleteMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectMvDirMessage200 = new HashMap<String, String>();
			expectMvDirMessage200.put("status", "200");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage200);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage404);
			tools.verifyResponse(setDeleteMethod(urlDestDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * ****mv dir ����200 �����ɹ� creat parent dir
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn200CreatParentDirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_srcmv2001";
		String destDir = "/dir_parent1/dir_dest/";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir + "?recursive=1";
		String urlDelDestDir= url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir;
		String urlDelDir_parent= url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir/dir_parent1";
		
		

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage404 = new HashMap<String, String>();
			expectDeleteMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectMvDirMessage200 = new HashMap<String, String>();
			expectMvDirMessage200.put("status", "200");

			System.out.println("the urlSrcDir   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			
			System.out.println("the urlDestDir   is : " + urlDestDir);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage200);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage404);
			tools.verifyResponse(setDeleteMethod(urlDelDestDir),
					expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlDelDir_parent),
					expectDeleteMessage200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * ***mv dir ����400 src dir ��dest dir ��ͬ
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn400SrcDestSameTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_srcmv400";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = urlSrcDir;
		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage404 = new HashMap<String, String>();
			expectDeleteMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectDeleteMessage400 = new HashMap<String, String>();
			expectDeleteMessage400.put("status", "400");
			// set expect response message
			Map<String, String> expectMvDirMessage200 = new HashMap<String, String>();
			expectMvDirMessage200.put("status", "200");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectDeleteMessage400);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * ***mv dir ����401 �ƶ�������appid�� ��Ȩ��
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn401DestAppidErrorTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_srcmv401";
		String destDir = "/dir_parent2/dir_dest/";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/2"
				+ "/1" + "/dir" + destDir + "?recursive=1";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			Map<String, String> expectMvDirMessage401 = new HashMap<String, String>();
			expectMvDirMessage401.put("status", "401");
			// set expect response message
			Map<String, String> expectMvDirMessage200 = new HashMap<String, String>();
			expectMvDirMessage200.put("status", "200");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage401);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}

	/*
	 * ***mv dir ����404 src��dest appid��ͬ ��Ȩ��
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn404SrcAppidErrorTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxB01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_src1";
		String destDir = "/dir_parent3/dir_dest/";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/2"
				+ "/1" + "/dir" + srcDir;
		tfsServer = tfs_NginxA01;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir + "?recursive=1";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			Map<String, String> expectMvDirMessage404 = new HashMap<String, String>();
			expectMvDirMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectMvDirMessage200 = new HashMap<String, String>();
			expectMvDirMessage200.put("status", "200");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage404);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * mv dir ����403 ��Ŀ¼�ƶ�����Ŀ¼��
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn403PutSonTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_srcmv403";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = urlSrcDir + "/son_dir";
		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectDeleteMessage400 = new HashMap<String, String>();
			expectDeleteMessage400.put("status", "400");
			// set expect response message
			Map<String, String> expectMvDirMessage403 = new HashMap<String, String>();
			expectMvDirMessage403.put("status", "403");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage403);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * mv dir ����404 srcĿ¼������
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn404SrcDirNonExsitTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_src2";
		String destDir = "/dir_dest2";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir;
		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectDeleteMessage400 = new HashMap<String, String>();
			expectDeleteMessage400.put("status", "400");
			// set expect response message
			Map<String, String> expectMvDirMessage404 = new HashMap<String, String>();
			expectMvDirMessage404.put("status", "404");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			// create dest dir		
			 tools.verifyResponse(setPostMethod(urlDestDir),
			 expectPostMessage);
			// mv to dest dir 
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage404);

			// tools.verifyResponse(setDeleteMethod(urlSrcDir),
			// expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlDestDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * mv dir ����404 Ŀ��Ŀ¼�Ѵ���
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn404DestDirExsitTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_src3";
		String destDir = "/dir_dest3";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir;
		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectDeleteMessage400 = new HashMap<String, String>();
			expectDeleteMessage400.put("status", "400");
			// set expect response message
			Map<String, String> expectMvDirMessage404 = new HashMap<String, String>();
			expectMvDirMessage404.put("status", "404");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			// create src dir
			// create dest dir
			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			tools.verifyResponse(setPostMethod(urlDestDir), expectPostMessage);
			// mv to dest dir and the dest dir is already exist
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage404);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlDestDir),
					expectDeleteMessage200);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * mv dir ����404 Ŀ�길Ŀ¼������
	 */
	@Test
	public void test_TFS_RestfulUserDefinedMvDirReturn404DestParentDirNonExsitTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String srcDir = "/dir_src4";
		String destDir = "/dir_destParent/dir_dest";
		String urlSrcDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + srcDir;
		String urlDestDir = url + "v2/" + tfsServer.getTfs_app_key() + "/1"
				+ "/1" + "/dir" + destDir;

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");
			// set expect response message
			Map<String, String> expectDeleteMessage200 = new HashMap<String, String>();
			expectDeleteMessage200.put("status", "200");

			// set expect response message
			Map<String, String> expectDeleteMessage404 = new HashMap<String, String>();
			expectDeleteMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectDeleteMessage400 = new HashMap<String, String>();
			expectDeleteMessage400.put("status", "404");
			// set expect response message
			Map<String, String> expectMvDirMessage404 = new HashMap<String, String>();
			expectMvDirMessage404.put("status", "404");

			System.out.println("the postUrl   is : " + urlSrcDir);
			/* do post file action */
			System.out.println("test keypoint  is  begining");
			// create src dir

			tools.verifyResponse(setPostMethod(urlSrcDir), expectPostMessage);
			// do not creat parrent dir
			// tools.verifyResponse(setPostMethod(urlDestDir),
			// expectPostMessage);
			// mv to dest dir and the dest dir is already exist
			tools.verifyResponse(setMvDirPostMethod(urlDestDir, srcDir),
					expectMvDirMessage404);

			tools.verifyResponse(setDeleteMethod(urlSrcDir),
					expectDeleteMessage200);
			tools.verifyResponse(setDeleteMethod(urlDestDir),
					expectDeleteMessage404);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * ls dir
	 */

	/*
	 * ls dir dir����dir����֤�Ƿ���ȷ
	 */
//bug ��Ҫ��֤
	@Test
	public void test_TFS_RestfulUserDefinedLsDirReturn200DirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_200ls";
		String urlLs = url + "v2/" + tfsServer.getTfs_app_key() + "/metadata"
				+ "/1" + "/1" + "/dir" + "/dir_200ls";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			// set expect response message
			Map<String, String> expectDirMessage = new HashMap<String, String>();
			expectDirMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			System.out.println("the urlLs   is : " + urlLs);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);
			tools.verifyResponse(setPostMethod(postMetaUrl + "/name"),
					expectPostMessage);

			// GetMethod getMethod = setGetMethod(urlLs);
			JSONArray fileJasonArray = tfsServer.getFileMetaInfo("/dir_200ls");
			// ȥ�� response�е�"[]"��������ת��restful���صĸ�ʽ
			String body = tools.setJavaResposeJsonForRestful(tools
					.trimFirstAndLastChar(fileJasonArray.toString(), '[', ']'));
			// set expect response message
			Map<String, String> expectResponseJasonMessage = new HashMap<String, String>();
			expectResponseJasonMessage.put("status", "200");
			expectResponseJasonMessage.put("body", body);
			System.out.println("test to string is " + body);

			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponseWithJSONWithoutBeginEndChar(
					setGetMethod(urlLs), expectResponseJasonMessage);

			tools.verifyResponse(setGetMethod(postMetaUrl + "/name"),
					expectDeleteMessage);

			tools.verifyResponse(setGetMethod(postMetaUrl), expectDirMessage);

			// delete
			tools.verifyResponse(setDeleteMethod(postMetaUrl + "/name"),
					expectDeleteMessage);
			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}



	/*
	 * ls dir dir
	 * url����
	 * ����400����
	 */

	@Test
	public void test_TFS_RestfulUserDefinedLsDirReturn400DirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
//		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
//				+ "/dir" + "/dir_201ls400";
		String urlLs = url + "v2/" + tfsServer.getTfs_app_key() + "/metadata"
				+ "/X" + "/1" + "/dir" + "/dir_201ls400";

		try { restartMeta();		

			System.out.println("the urlLs   is : " + urlLs);
			/* do post file action */
			System.out.println("creat dir  begin");			

			// set expect response message
			Map<String, String> expectMessage400 = new HashMap<String, String>();
			expectMessage400.put("status", "400");

			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponse(setGetMethod(urlLs), expectMessage400);

			// delete

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		}
	}
	
	
	
	
	@Test
	public void test_TFS_RestfulUserDefinedLsDirReturn404Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_201ls404";
		String urlLs = url + "v2/" + tfsServer.getTfs_app_key() + "/metadata"
				+ "/1" + "/1" + "/dir" + "/dir_201ls404";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");

			// set expect response message
			Map<String, String> expectDirMessage = new HashMap<String, String>();
			expectDirMessage.put("status", "200");
			

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			System.out.println("the urlLs   is : " + urlLs);
			/* do post file action */
			System.out.println("creat dir  begin");
			

			// set expect response message
			Map<String, String> expectResponseJasonMessage = new HashMap<String, String>();
			expectResponseJasonMessage.put("status", "404");

			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponseWithJSONWithoutBeginEndChar(
					setGetMethod(urlLs), expectResponseJasonMessage);

			tools.verifyResponse(setGetMethod(postMetaUrl + "/name"),
					expectResponseJasonMessage);

			tools.verifyResponse(setGetMethod(postMetaUrl), expectResponseJasonMessage);

			// delete
			tools.verifyResponse(setDeleteMethod(postMetaUrl + "/name"),
					expectResponseJasonMessage);
			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectResponseJasonMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}
	
	
	
	@Test
	public void test_TFS_RestfulUserDefinedCheckDirReturn400Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/X" + "/1"
				+ "/dir" + "/dir_201";

		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");
			// set expect response message
			Map<String, String> expectHeadMessage400 = new HashMap<String, String>();
			expectHeadMessage400.put("status", "400");
			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");
			// set expect response message
			Map<String, String> expectDirMessage = new HashMap<String, String>();
			expectDirMessage.put("status", "200");
			

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
		
			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponse(headMethod(postMetaUrl), expectHeadMessage400);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}
	
	
	/*
	 * head dir ���ָ���Ƿ����Ŀ¼��
	 * ����Ŀ¼�����ڣ�
	 * ����404 
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCheckDirReturn404Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_201check404";

		try { restartMeta();

	
			// set expect response message
			Map<String, String> expectHeadMessage404 = new HashMap<String, String>();
			expectHeadMessage404.put("status", "404");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
		
			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponse(headMethod(postMetaUrl), expectHeadMessage404);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}
	
	
	/*
	 * head dir��
	 * ����200��ȷ
	 */

	@Test
	public void test_TFS_RestfulUserDefinedHeadDirReturn200DirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_201head200";


		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");
			// set expect response message
			Map<String, String> expectHeadMessage200 = new HashMap<String, String>();
			expectHeadMessage200.put("status", "200");
			// set expect response message
			Map<String, String> expectDirMessage = new HashMap<String, String>();
			expectDirMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);
			tools.verifyResponse(setPostMethod(postMetaUrl + "/name"),
					expectPostMessage);
	
			

			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponse(headMethod(postMetaUrl), expectHeadMessage200);

			// delete
			tools.verifyResponse(setDeleteMethod(postMetaUrl + "/name"),
					expectDeleteMessage);
			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}

	/*
	 * head dir��
	 * ���ļ�������404 Ŀ¼������
	 * ����200��ȷ
	 */

	@Test
	public void test_TFS_RestfulUserDefinedHeadDirReturn404DirTest() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = NGINX.getRoot_url_adress();
		String urlMeta = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"
				+ "/dir" + "/dir_201head404";
		String urlFile = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"+"/file"
				 + "/dir_201head404";
		String urlDelFile = url + "v2/" + tfsServer.getTfs_app_key() + "/1" + "/1"+"/file"
				+ "/dir_201head404" + "/name";


		try { restartMeta();

			// set expect response message
			Map<String, String> expectPostMessage = new HashMap<String, String>();
			expectPostMessage.put("status", "201");

			// set expect response message
			Map<String, String> expectDeleteMessage = new HashMap<String, String>();
			expectDeleteMessage.put("status", "200");
			// set expect response message
			Map<String, String> expectHeadMessage200 = new HashMap<String, String>();
			expectHeadMessage200.put("status", "200");
			// set expect response message
			Map<String, String> expectHeadMessage404 = new HashMap<String, String>();
			expectHeadMessage404.put("status", "404");
			// set expect response message
			Map<String, String> expectDirMessage = new HashMap<String, String>();
			expectDirMessage.put("status", "200");

			String postMetaUrl = urlMeta;
			System.out.println("the postUrl   is : " + postMetaUrl);
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setPostMethod(postMetaUrl), expectPostMessage);
			tools.verifyResponse(setPostMethod(urlFile + "/name"+"?recursive=0"),
					expectPostMessage);
	
			

			// ��֤get dir �����Ƿ���ȷ
			tools.verifyResponse(headMethod(postMetaUrl+ "/name"), expectHeadMessage404);

			// delete
			tools.verifyResponse(setDeleteMethod(urlDelFile ),
					expectDeleteMessage);
			tools.verifyResponse(setDeleteMethod(postMetaUrl),
					expectDeleteMessage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); Assert.assertTrue(false);
		} 
	}
	
}
