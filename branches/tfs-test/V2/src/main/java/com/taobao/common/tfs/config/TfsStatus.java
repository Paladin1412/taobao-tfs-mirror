package com.taobao.common.tfs.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;

import com.taobao.common.tfs.utility.MySQLConnector;

public class TfsStatus {
	private Connection conn;
	
	public TfsStatus(){
		String server = "10.232.4.28:3306";
		String dbName = "tfs_stat";
		String user = "foobar";
		String password = "foobar";
		
		conn = MySQLConnector.getConnection(server,dbName,user, password);
	}
	
	public long getCurrentQuote(String appKey) {
		String statement = "select USED_CAPACITY from t_app_stat, t_app_info " +
				"where t_app_stat.APP_ID=t_app_info.ID and APP_KEY='"+appKey+"'";
		Object value = getColumnValue(statement, "USED_CAPACITY");
		
		return parseLong(value);
	}
	
	public long getMaxQuote(String appKey) {
		String statement = "select quto from t_app_info where app_key='"+appKey+"'";
		Object value = getColumnValue(statement, "quto");
		
		return parseLong(value);
	}
	
	public void modifyMaxQuote(String appKey,int quote){
		String statement = "update t_app_info set quto="+quote+" where APP_KEY=\""+appKey+"\"";
		
		update(statement);
		refreshLastUpdateTime();
	}
	
	public void resetCurrentQuote(String appKey){
		String statement = "select id from t_app_info where app_key=\""+appKey+"\"";
		int appID = parseInt(getColumnValue(statement, "id"));
		
		//statement = "update t_app_stat set USED_CAPACITY=0, FILE_COUNT=0 where APP_ID="+appID;
		statement = "delete from t_app_stat";
		update(statement);
		
		statement = "delete from t_session_stat where SESSION_ID like "+"\""+appID+"-%\"";
		update(statement);
		
		refreshLastUpdateTime();
	}
	
	public void setGroupPermission(String appKey,int mode){
		String statement = "update t_cluster_rack_group set CLUSTER_RACK_ACCESS_TYPE="+mode
				+", modify_time=now() where CLUSTER_GROUP_ID=" +
				"(select CLUSTER_GROUP_ID from t_app_info where APP_KEY=\""+appKey+"\")";
		
		update(statement);
		refreshLastUpdateTime();
	}
	
	
	public void setClusterPermissionByAppKey(String appKey, int mode) {
		String statement = "update t_cluster_rack_info set CLUSTER_STAT="+mode+", " +
				"modify_time=now() where CLUSTER_RACK_ID in " +
				"(select CLUSTER_RACK_ID from t_cluster_rack_group where " +
				"CLUSTER_GROUP_ID=(select CLUSTER_GROUP_ID from t_app_info where APP_KEY=\""+appKey+"\"))";
		
		update(statement);
		refreshLastUpdateTime();
	}
	
	public void setGroupPermissionByNs(String nsAddr,int mode){
		String statement = "update t_cluster_rack_info set CLUSTER_STAT="+mode+", " +
				"modify_time=now() where ns_vip = \""+nsAddr+"\"";
		
		update(statement);
		refreshLastUpdateTime();
	}
	
	public boolean checkSessionId(String sessionId) {
		String statement = "select cache_size from t_session_info where session_id=\""+sessionId+"\"";
		Object value = getColumnValue(statement, "cache_size");
		
		if(parseLong(value)>-1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean setDuplicateStatus(String appKey, int status) {
		String statement = "update t_app_info set need_duplicate="+status+", MODIFY_TIME=now() " +
				"where app_key=\""+appKey+"\"";
		
		boolean result = update(statement);
		if(!result){
			return result;
		}
		
		return refreshLastUpdateTime();
	}
	
	public boolean changeDuplicateServerAddr(int clusterRackID, String addr){
		String statement = "update t_cluster_rack_duplicate_server set dupliate_server_addr=\""+addr+"\", " +
				"modify_time=now() where cluster_rack_id="+clusterRackID;
		
		boolean result = update(statement);
		if(!result){
			return result;
		}
		
		return refreshLastUpdateTime();
	}
	
	public long getUsedCapacity(String appKey) {
		String statement = "select used_capacity from t_app_stat " +
				"where app_id=(select id from t_app_info where app_key=\""+appKey+"\")";
		
		Object value = getColumnValue(statement, "used_capacity");
		
		return parseLong(value);
	}
	
	public long getFileCount(String appKey) {
		String statement = "select FILE_COUNT from t_app_stat where " +
				"APP_ID=(select ID from t_app_info where APP_KEY=\""+appKey+"\")";
		
		Object value = getColumnValue(statement, "FILE_COUNT");
		
		return parseLong(value);
	}
	
	public long getFileSize(String sessionId, int operType) {
		String statement = "select file_size from t_session_stat where " +
				"SESSION_ID=\""+sessionId+"\" and OPER_TYPE="+operType;
		
		Object value = getColumnValue(statement, "file_size");
		
		return parseLong(value);
	}
	
	
	private int parseInt(Object obj){
		if(obj==null){
			return -1;
		}else{
			return ((Integer)obj).intValue();
		}
	}
	
	private long parseLong(Object obj){
		if(obj==null){
			return -1;
		}else{
			return ((Long)obj).longValue();
		}
	}
	
	private boolean refreshLastUpdateTime(){
		String statement = "update t_base_info_update_time " +
				"set APP_LAST_UPDATE_TIME=now(), BASE_LAST_UPDATE_TIME=now()";
		
		return update(statement);
	}
	
	private boolean update(String statement){
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(statement);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	
	private Object getColumnValue(String statement,String col){
		Statement stmt = null;
		ResultSet result = null;
		try {
			stmt = conn.createStatement();
			result = stmt.executeQuery(statement);
			if(!result.next()){
				return null;
			}else{
				return result.getObject(col);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			try {
				if (result != null) {
					result.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
	}
	
	@Test
	public void testGetMaxQuote(){
		System.out.println("get max quote: "+getMaxQuote("testKeyYS"));
	}
	
	@Test
	public void testGetCurrentQuote(){
		System.out.println("get current quote: "+getCurrentQuote("foobarRcAA"));
	}
	
	@Test
	public void testRefreshLastUpdateTime(){
		refreshLastUpdateTime();
	}
	
	@Test
	public void testModifyMaxQuote(){
		modifyMaxQuote("testKeyYS", 30);
	}
	
	@Test
	public void testResetCurrentQuote(){
		resetCurrentQuote("testKeyYS");
	}
	
	@Test
	public void testSetGroupPermission(){
		//resetCurrentQuote("foobarRcAA");
		setGroupPermission("foobarRcAA", 2);
	}
	
	@Test
	public void testSetClusterPermission(){
		setClusterPermissionByAppKey("foobarRcAA", 2);
	}
	
	@Test
	public void testSetDuplicateStatus(){
		setDuplicateStatus("foobarRcAA", 0);
	}
	
	@Test
	public void testGetUsedCapacity(){
		System.out.println(getUsedCapacity("foobarRcAA"));
	}
}