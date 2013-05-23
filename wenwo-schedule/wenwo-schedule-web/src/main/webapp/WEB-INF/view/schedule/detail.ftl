[#ftl]
<table>
	<tr><td>任务id: </td><td>${job.id}</td></tr>
	<tr><td>任务说明: </td><td>${job.desc}</td></tr>
	<tr><td>执行主类: </td><td>${job.mainClass}</td></tr>
	<tr><td>任务属主: </td><td>${job.owner}</td></tr>
	<tr><td>任务分组: </td><td>${job.group}</td></tr>
	<tr><td>调度时机: </td><td>${job.cron}</td></tr>
	<tr><td>启动时间: </td><td>${info.startTime?string('yyyy-MM-dd HH:mm:ss')}</td></tr>
	<tr><td>上次触发时间: </td><td>${(info.previousFireTime?string('yyyy-MM-dd HH:mm:ss'))!''}</td></tr>
	<tr><td>下次触发时间: </td><td>${info.nextFireTime?string('yyyy-MM-dd HH:mm:ss')}</td></tr>
</table>