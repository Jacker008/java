[#ftl]
[#import 'common.ftl' as c]
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>调度任务列表</title>
		[@c.frame_header /]
		[@c.css file='schedule.css' /]
		[@c.css file='cron-picker.css' /]
		[@c.js file='schedule.js' /]
		[@c.js file='public/jquery.form.js' /]
		[@c.js file='cron-picker.js' /]
		<script type="text/javascript">
			$(function(){
				Schedule.init();
				CronPicker.init('new_job_cron');
				$('#schedule_menu').click();
			});
		</script>
	</head>
	<body>
		[@c.frame role=user.role]
			<p><a href="#" id="new_job">添加任务</a></p>
			<p>
			<div id="tabs">
			<ul>
				[#list owners as owner]
				<li><a href="#${owner}">${owner}</a></li>
				[/#list]
			</ul>
				[#list owners as owner]
					<div id="${owner}"></div>
				[/#list]
			</div>
			</p>
		[/@c.frame]
		<div id="new_job_dialog">
			<form action="/schedule/create" id="new_job_form" enctype="multipart/form-data" accept-charset="UTF-8" method="post">
				<table border="0">
					<tr><td>任务说明: </td><td><input type="text" name="desc" class="text_field"/></td></tr>
					<tr><td>执行主类: </td><td><input type="text" name="mainClass" class="text_field"/></td></tr>
					<tr><td>任务分组: </td><td><input type="text" name="group" class="text_field"/></td></tr>
					<tr><td>任务包: </td><td><input type="file" name="file" class="text_field"/></td></tr>
					<tr><td colspan="2" height="200px">[@c.cron_picker id="new_job_cron" /]</td></tr>
					<tr><td colspan="2" align="right"><input type="submit" value="创建任务" /></td></tr>
				</table>
			</form>
		</div>
		<div id="job_detail_dialog">
		</div>
		<div id="job_update_dialog">
		</div>
	</body>
</html>