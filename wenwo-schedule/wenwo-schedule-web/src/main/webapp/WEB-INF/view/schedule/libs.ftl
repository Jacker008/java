[#ftl]
[#import 'common.ftl' as c]
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>公共包列表</title>
		[@c.frame_header /]
		[@c.js file='public/jquery.form.js' /]
		[@c.js file='schedule-lib.js' /]
		<script type="text/javascript">
			$(function(){
				ScheduleLib.init();
				$('#schedule_menu').click();
			});
		</script>
	</head>
	<body>
		[@c.frame role=user.role]
			<p><a href="#" id="new_lib" style="margin-left:17px">上传包</a></p>
			<p>
				<table id="libs_tb">
					<tr><th>包名</th><th>修改时间</th><th>大小</th><th>操作</th></tr>
					[#list libs as lib]
						<tr>
							<td>${lib.name}</td>
							<td>${lib.modifyTime?string('yyyy-MM-dd HH:mm:ss')}</td>
							<td>${lib.size/1024} kb</td>
							<td><a href="#" lib_remove_id="${lib.name}">删除</a></td>
						</tr>
					[/#list]
				</table>
			</p>
		[/@c.frame]
		<div id="upload_lib_dialog">
			<form id="upload_lib_form" action="/schedule/upload_lib">
				<input type="file" name="file"/>
				<input type="submit" value="上传"/>
			</form>
		</div>
	</body>
</html>