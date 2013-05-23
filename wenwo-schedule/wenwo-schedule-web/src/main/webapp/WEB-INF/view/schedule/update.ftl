[#ftl]
[#import 'common.ftl' as c]
<form action="/schedule/update" id="update_job_form" enctype="multipart/form-data" accept-charset="UTF-8" method="post">
	<table border="0">
		<tr><td>任务id: </td><td><input type="text" name="id" value="${job.id}" readonly="readonly" class="text_field"/></td></tr>
		<tr><td>任务说明: </td><td><input type="text" name="desc" value="${job.desc}" class="text_field"/></td></tr>
		<tr><td>执行主类: </td><td><input type="text" name="mainClass" value="${job.mainClass}" class="text_field"/></td></tr>
		<tr><td>任务分组: </td><td><input type="text" name="group" value="${job.group}" class="text_field"/></td></tr>
		<tr><td>任务包: </td><td><input type="file" name="file" class="text_field"/></td></tr>
		<tr><td colspan="2" height="200px">[@c.cron_picker id="update_job_cron" cron=job.cron /]</td></tr>
		<tr><td colspan="2" align="right"><input type="submit" value="修改" /></td></tr>
	</table>
</form>