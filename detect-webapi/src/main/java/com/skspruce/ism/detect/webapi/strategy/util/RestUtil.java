package com.skspruce.ism.detect.webapi.strategy.util;

import com.skspruce.ism.detect.webapi.strategy.RestConstants;
import com.skspruce.ism.detect.webapi.strategy.vo.Message;
import com.skspruce.ism.detect.webapi.strategy.vo.ResponseMessage;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

public class RestUtil {
	private static Logger logger = LoggerFactory.getLogger(RestUtil.class);


	/**
	 * 返回消息对象中追加返回消息状态.
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addResponseMessageForModelMap(JSONObject model) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage(new Message("操作成功", "suecces"));
		model.put(RestConstants.ReturnResponseMessage, responseMessage);
		return responseMessage;
	}



	/**
	 * put post请求时,业务数据放到内容区,json格式.
	 * 
	 * @param request
	 * @return
	 */
	static public String getAttJsonStringByRequest(HttpServletRequest request) {
		String syncRequestString = "";
		try {
			// // request.getParameter 读取URL参数.....
			// // 读取请求内容，转换为String
			syncRequestString = (String)request.getAttribute("json");
		} catch (Exception e) {
		} finally {

		}

		return syncRequestString;
	}

	public Field getFileByPropertyName(String propertyName, Class clazz) {
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
