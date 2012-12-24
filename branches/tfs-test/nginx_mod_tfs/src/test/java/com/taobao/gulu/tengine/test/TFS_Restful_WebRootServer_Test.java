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

/* web root server ceshi
 * 
 * ���ڿ����������ļ�������net_mask���������룩��
 * wrs����������ip��ַ�������������м���õ������ַ�����Σ���
 * Ȼ�����ݺ�˽������Ľ����״̬�������������������ͬһ�����ε�nginx��ַ���ء�
 * ע�⣬������Ҫ���nginx��ַ̫�٣����������ļ������õ�ĳ��ֵleast_rtn_addr_count��
 * �᷵������״̬������nginx��ַ��
 * 
 */
public class TFS_Restful_WebRootServer_Test extends BaseCase {
	/*
	 * 
	 * 
	 * 10.232.4.6��10.232.4.35
	 */

	/*
	 * create file ����201
	 */
	@Test
	public void test_TFS_RestfulUserDefinedCreateFileReturn201Test() {

		VerifyTool tools = new VerifyTool();

		/* set base url */
		TFS tfsServer = tfs_NginxA01;
		String url = "http://10.232.4.35:3800";
		String urlWebRoot = url +  "/tfs.list";
		try {
			// set expect response message
			Map<String, String> expectMessage200 = new HashMap<String, String>();
			expectMessage200.put("body", "10.232.4.35:8032\n10.232.4.6:8032");
			expectMessage200.put("status", "200");
	
			System.out.println("the urlWebRoot   is : " + urlWebRoot);
			
			/* do post file action */
			System.out.println("creat dir  begin");
			tools.verifyResponse(setGetMethod(urlWebRoot), expectMessage200);

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

}
