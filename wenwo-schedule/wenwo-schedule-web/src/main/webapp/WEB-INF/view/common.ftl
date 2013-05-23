[#ftl]

[#macro js file]
	<script src="${rc.getContextPath()}/js/${file}"></script>
[/#macro]

[#macro css file]
	<link href="${rc.getContextPath()}/css/${file}" rel="stylesheet">
[/#macro]

[#macro cron_picker id cron='* * * * * ? *']
	<div id="${id}">
	  <ul>
	    <li><a href="#${id}-1"><span>年份</span></a></li>
	    <li><a href="#${id}-2"><span>星期</span></a></li>
	    <li><a href="#${id}-3"><span>月</span></a></li>
	    <li><a href="#${id}-4"><span>天</span></a></li>
	    <li><a href="#${id}-5"><span>小时</span></a></li>
	    <li><a href="#${id}-6"><span>分钟</span></a></li>
	    <li><a href="#${id}-7"><span>秒</span></a></li>
	  </ul>
	  <div id="${id}-1">
	    <p><input name="year" type="radio" checked="checked"/>每年</p>
	  </div>
	  <div id="${id}-2">
	    <p><input name="week" type="radio" value="every" checked="checked"/>每周</p>
	    <p><input name="week" type="radio" value="manual" />手选</p>
	    <input name="weeks" type="checkbox" value="1"/>周日
	    <input name="weeks" type="checkbox" value="2"/>周一
	    <input name="weeks" type="checkbox" value="3"/>周二
	    <input name="weeks" type="checkbox" value="4"/>周三
	    <input name="weeks" type="checkbox" value="5"/>周四
	    <input name="weeks" type="checkbox" value="6"/>周五
	    <input name="weeks" type="checkbox" value="7"/>周六
	  </div>
	  <div id="${id}-3">
	  	<p><input name="month" type="radio" value="every" checked="checked"/>每月</p>
	    <p><input name="month" type="radio" value="manual" />手选</p>
	    [#list 1..12 as month_num]
	    	<input name="months" type="checkbox" value="${month_num}"/>${month_num}
	    [/#list]
	  </div>
	  <div id="${id}-4">
	  	<p><input name="day" type="radio" value="every" checked="checked"/>每天</p>
	    <p><input name="day" type="radio" value="manual" />手选</p>
	    [#list 1..31 as day_num]
	    	<input name="days" type="checkbox" value="${day_num}"/>${day_num}
	    [/#list]
	  </div>
	  <div id="${id}-5">
	  	<p><input name="hour" type="radio" value="every" checked="checked"/>每小时</p>
	    <p><input name="hour" type="radio" value="manual" />手选</p>
	    [#list 0..23 as hour_num]
	    	<input name="hours" type="checkbox" value="${hour_num}"/>${hour_num}
	    [/#list]
	  </div>
	  <div id="${id}-6">
	  	<p><input name="minute" type="radio" value="every" checked="checked"/>每分钟</p>
	  	<p><input name="minute" type="radio" value="everyN"/>
	  	<input type="text" value="0" name="time_start" disabled="disabled" class="num_picker"/>分开始
	  	 - 每<input type="text" value="2" name="time_every" disabled="disabled" class="num_picker"/>分钟
	  	</p>
	  </div>
	  <div id="${id}-7">
	  	<p><input name="second" type="radio" value="every" checked="checked"/>每秒</p>
	  	<p><input name="second" type="radio" value="everyN"/>
	  	<input type="text" value="0" name="time_start" disabled="disabled" class="num_picker"/>秒开始
	  	 - 每<input type="text" value="5" name="time_every" disabled="disabled" class="num_picker"/>秒
	  	</p>
	  </div>
		<div class="cron_express"><span>调度时机：</span><input type="text" name="cron" style="width:300px;" readonly="readonly" value="${cron}"/></div>
	</div>
[/#macro]

[#macro frame_header]
	[@css file='common.css' /]
	[@css file='menu.css' /]
	[@css file='jquery-ui.css' /]
	[@js file='public/jquery.js' /]
	[@js file='public/jquery-ui.js' /]
	[@js file='public/jquery.layout.js' /]
	[@js file='public/jquery.multinav.js' /]
	<script type="text/javascript">
	    $(function(){
		    $(document).ready(function () {
		        $('body').layout({ applyDefaultStyles: true });
		    });
			$("#nav_left").multiNav();
		});
	</script>
[/#macro]
[#macro frame role]
	<div class="ui-layout-center">
		[#nested]
	</div>
	<div class="ui-layout-north"><h1>调度 & 监控系统</h1></div>
	<div class="ui-layout-west">[@menu role=role /]</div>
[/#macro]

[#macro menu role]
	<ul id="nav_left" class="nav_left">
	    <li><a href="#" id="schedule_menu">调度管理</a>
	        <ul>
	            <li><a href="/schedule/list" id="schedule_list">调度列表</a></li> 
	            <li><a href="/schedule/libs" id="schedule_libs">公共包</a></li> 
	        </ul> 
	    </li>
	    [#if role == "ADMIN"]
	    <li><a href="#">监控管理</a>
	        <ul>
	            <li><a href="#" id="">监控列表</a></li> 
	        </ul>
	    </li>
	    <li><a href="#" id="user_menu">帐号管理</a>
	        <ul>
	            <li><a href="/user/list" id="user_list">帐号列表</a></li> 
	        </ul>
	    </li>
	    [/#if]
	</ul>
[/#macro]