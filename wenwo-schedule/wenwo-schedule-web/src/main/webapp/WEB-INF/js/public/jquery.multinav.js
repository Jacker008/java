/*
垂直二级导航切换
http://code.ciaoca.cn/
日期：2011-12-15

settings 参数说明
-----
events:按钮事件
speed:切换速度
------------------------------ */
(function($){
	$.fn.multiNav=function(settings){
		if(this.length<1){return;};

		// 默认值
		settings=$.extend({
			events:"click",
			speed:600
		},settings);

		var nav_obj=this;

		nav_obj.delegate("li",settings.events,function(e){
			e.stopPropagation();
			var li=$(this).closest("li");
			if(li.children("ul").length>0){
				var li_arr=li.siblings();

				li_arr.removeClass("selected");
				li_arr.find("li").removeClass("selected");
				li_arr.find("ul").slideUp(settings.speed);
				if(li.hasClass("selected")){
					li.find("ul").slideUp(settings.speed);
					li.removeClass("selected");
					return false;
				};
				li.toggleClass("selected");
				li.children("ul").slideToggle(settings.speed);
				return false;
			};
		});
	};
})(jQuery);