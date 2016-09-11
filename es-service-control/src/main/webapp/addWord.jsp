<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>后台词库添加</title>
<script language="javascript">
	var xmlHttp;
	//创建xmlHttp
	function createXMLHttpRequest() {
		if (window.ActiveXObject) {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} else if (window.XMLHttpRequest) {
			xmlHttp = new XMLHttpRequest();
		}
	}

	//使用post方式发送
	function doRequestUsingPost() {
		createXMLHttpRequest();
		var tag = document.getElementById("tag").value;
		var action = document.getElementById("action").value;
		var word = document.getElementById("word").value;
		var url = "AddWordServlet?word=" + word + "&tag=" + tag + "&action="
				+ action;
		url = encodeURI(url);
		url = encodeURI(url);
		xmlHttp.open("POST", url, true);
		xmlHttp.onreadystatechange = handleStateChange;
		xmlHttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=UTF-8");
		xmlHttp.send(null);
	}

	function handleStateChange() {
		if (xmlHttp.readyState == 4) {
			if (xmlHttp.status == 200) {
				parseResults();
			}
		}
	}

	function parseResults() {
		var responseDiv = document.getElementById("serverResponse");
		if (responseDiv.hasChildNodes()) {
			responseDiv.removeChild(responseDiv.childNodes[0]);
		}
		//var responseText = document.createTextNode(xmlHttp.responseText);
		var responseText = xmlHttp.responseText;
		if (responseText.indexOf("\n") > 0) {
			responseText = responseText.replace(/\n/g, "<br/>");
		}
		//alert("后台返回的返回值： " + xmlHttp.responseText);
		//responseDiv.appendChild(responseText);
		responseDiv.innerHTML = responseText;
	}
</script>
</head>

<body>


	<form id="form1" name="form1" method="post" action="#"
		style="text-align: center;">
		<table align="center" width="600px" border="1"
			style="margin-top: 50px;">
			<tr align="left">
				<td colspan="2"><h3>
						<a href="index.jsp">首页</a>
					</h3></td>
			</tr>
			<tr align="left">
				<td>选择操作文件：</td>
				<td><select id="tag" name="tag">
						<option value="remote_ext_dict.txt">扩展词-词典</option>
						<option value="remote_ext_stopwords.txt">停用词-词典</option>
				</select></td>
			</tr>
			<tr align="left">
				<td>操作类型：</td>
				<td><select id="action" name="action">
						<option value="add">增加</option>
						<option value="delete">删除</option>
				</select></td>
			</tr>
			<tr align="left">
				<td>输入内容：</td>
				<td><p style="color: red;">
						注意:此内容可以用-符号分割批量操作。<br />例如:词条1-词条2-词条3
					</p> <textarea rows="10" cols="60" id="word" name="word"></textarea></td>
			</tr>
			<tr align="left">
				<td>提交按钮：</td>
				<td><input type="button" name="Submit2" value="POST"
					onclick="doRequestUsingPost();" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<div id="serverResponse" style="color: blue;"></div>
				</td>
			</tr>
		</table>
	</form>

</body>
</html>


