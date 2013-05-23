[#ftl]
<table name="jobs_tb">
	<tr>
		<th>任务说明</th>
		<th>操        作</th>
		<th>执行主类</th>
		<th>调度时机</th>
	</tr>
	[#list groups?keys as group]
		<tr>
			<th colspan="5">分组 - [${group}]</th>
		</tr>
		[#assign groupJobs=groups[group] /]
		[#list groupJobs as job]
			<tr>
				<td>${job.desc}</td>
				<td>
					<a href="#" view_id="${job.id}">任务信息</a> 
					<a href="#" trigger_id="${job.id}">手动执行</a>
					<br>
					<a href="#" update_id="${job.id}">修改</a>
					<a href="#" remove_id="${job.id}" job_desc="${job.desc}">删除</a>
					[#if job.paused]
					<a href="#" color="red" resume_id="${job.id}">启动</a>
					[#else]
					<a href="#" color="red" pause_id="${job.id}">暂停</a>
					[/#if]
				</td>
				<td>${job.mainClass}</td>
				<td>${job.cron}</td>
			</tr>
		[/#list]
	[/#list]
</table>