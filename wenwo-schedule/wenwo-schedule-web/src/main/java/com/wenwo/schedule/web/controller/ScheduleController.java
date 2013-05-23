package com.wenwo.schedule.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.wenwo.commons.util.MapUtil;
import com.wenwo.schedule.sdk.CommonLib;
import com.wenwo.schedule.sdk.IScheduleJobService;
import com.wenwo.schedule.sdk.JobExecuteInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo.FileType;
import com.wenwo.schedule.web.dao.UserDao;
import com.wenwo.schedule.web.lang.ResponseContent;
import com.wenwo.schedule.web.model.User;

/**
 * @author yuxuan.wang
 * 
 */
@Controller
public class ScheduleController {

	@Resource(name = "scheduleJobService")
	private IScheduleJobService scheduleJobService;

	@Autowired
	private UserDao userDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

	/**
	 * 创建任务
	 * 
	 * @param request
	 * @param scheduleJob
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "schedule/create", method = RequestMethod.POST)
	public @ResponseBody
	ResponseContent create(HttpServletRequest request, ScheduleJobInfo<?> scheduleJob, @RequestParam("file") MultipartFile file) {
		User logonUser = (User) request.getSession().getAttribute("user");
		scheduleJob.setOwner(logonUser.getUsername());
		try {
			byte[] fileData = file.getBytes();
			String fileName = file.getOriginalFilename();
			scheduleJob.setFileType(FileType.detect(fileName));
			scheduleJobService.uploadJob(scheduleJob, fileData);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return new ResponseContent(true, "添加任务成功");
	}

	/**
	 * 更新任务
	 * 
	 * @param request
	 * @param scheduleJob
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "schedule/update", method = RequestMethod.POST)
	public @ResponseBody
	ResponseContent update(HttpServletRequest request, ScheduleJobInfo<?> scheduleJob,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		byte[] fileData = null;
		try {
			fileData = file == null ? null : file.getBytes();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		FileType fileType = file == null ? null : FileType.detect(file.getOriginalFilename());
		scheduleJobService.updateJob(scheduleJob.getId(), scheduleJob.getDesc(), scheduleJob.getGroup(), scheduleJob.getMainClass(),
				scheduleJob.getCron(), fileData, fileType);
		return new ResponseContent(true, "修改任务成功");
	}

	/**
	 * 获取任务信息
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/detail", method = RequestMethod.GET)
	public ModelAndView detail(String jobId) {
		ScheduleJobInfo<?> job = scheduleJobService.find(jobId);
		JobExecuteInfo info = scheduleJobService.jobInfo(jobId);
		return new ModelAndView("schedule/detail", MapUtil.asMap(new String[] { "job", "info" }, new Object[] { job, info }));
	}

	/**
	 * 获取任务信息
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/remove", method = RequestMethod.GET)
	public @ResponseBody
	ResponseContent remove(String jobId) {
		scheduleJobService.removeJob(jobId);
		return new ResponseContent(true, "删除成功");
	}

	/**
	 * 暂停任务
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/pause", method = RequestMethod.GET)
	public @ResponseBody
	ResponseContent pause(String jobId) {
		scheduleJobService.pauseJob(jobId);
		return new ResponseContent(true, "暂停成功");
	}

	/**
	 * 启动任务
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/resume", method = RequestMethod.GET)
	public @ResponseBody
	ResponseContent resume(String jobId) {
		scheduleJobService.resumeJob(jobId);
		return new ResponseContent(true, "启动成功");
	}

	/**
	 * 手动执行任务
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/trigger", method = RequestMethod.GET)
	public @ResponseBody
	ResponseContent trigger(String jobId) {
		scheduleJobService.triggerJob(jobId);
		return new ResponseContent(true, "手动执行成功");
	}

	/**
	 * 获取更新前任务初始信息
	 * 
	 * @param jobId
	 * @return
	 */
	@RequestMapping(value = "schedule/before_update", method = RequestMethod.GET)
	public ModelAndView beforeUpdate(String jobId) {
		ScheduleJobInfo<?> job = scheduleJobService.find(jobId);
		return new ModelAndView("schedule/update", "job", job);
	}

	/**
	 * 调度列表
	 * 
	 * @param request
	 */
	@RequestMapping("schedule/list")
	public ModelAndView list(HttpServletRequest request) {
		User logonUser = (User) request.getSession().getAttribute("user");
		List<String> owners = new ArrayList<String>();
		// 管理员可以查看所有调度任务
		if (User.Role.ADMIN == logonUser.getRole()) {
			Sort sort = new Sort(Direction.ASC, "role");
			Iterable<User> all = userDao.findAll(sort);
			for (User item : all)
				owners.add(item.getUsername());
		} else {
			owners.add(logonUser.getUsername());
		}
		return new ModelAndView("schedule/list", "owners", owners);
	}

	/**
	 * 调度列表
	 * 
	 * @param request
	 */
	@RequestMapping("schedule/refresh_list")
	public ModelAndView refreshList(HttpServletRequest request, String owner) {
		User logonUser = (User) request.getSession().getAttribute("user");
		Iterable<ScheduleJobInfo<?>> records = null;
		// 管理员可以查看所有调度任务
		if (User.Role.ADMIN == logonUser.getRole()) {
			records = scheduleJobService.all();
		} else {
			records = scheduleJobService.owner(logonUser.getUsername());
		}
		return new ModelAndView("schedule/refresh_list", "groups", toMap(records).get(owner));
	}

	/**
	 * 分组任务以便页面显示
	 * 
	 * @param records
	 * @return
	 */
	private Map<String, Map<String, List<ScheduleJobInfo<?>>>> toMap(Iterable<ScheduleJobInfo<?>> records) {
		Iterator<ScheduleJobInfo<?>> it = records.iterator();
		Map<String, Map<String, List<ScheduleJobInfo<?>>>> jobs = new HashMap<String, Map<String, List<ScheduleJobInfo<?>>>>();
		while (it.hasNext()) {
			ScheduleJobInfo<?> job = it.next();
			String owner = job.getOwner();
			Map<String, List<ScheduleJobInfo<?>>> groups = jobs.get(owner);
			if (groups == null) {
				groups = new HashMap<String, List<ScheduleJobInfo<?>>>();
				jobs.put(owner, groups);
			}
			String group = job.getGroup();
			List<ScheduleJobInfo<?>> items = groups.get(group);
			if (items == null) {
				items = new ArrayList<ScheduleJobInfo<?>>();
				groups.put(group, items);
			}
			items.add(job);
		}
		return jobs;
	}

	private Comparator<CommonLib> clibComparator = new Comparator<CommonLib>() {

		@Override
		public int compare(CommonLib o1, CommonLib o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * 获取公共包列表
	 * 
	 * @return
	 */
	@RequestMapping("schedule/libs")
	public ModelAndView libs() {
		List<CommonLib> libs = scheduleJobService.libs();
		Collections.sort(libs, clibComparator);
		return new ModelAndView("schedule/libs", "libs", libs);
	}

	@RequestMapping("schedule/upload_lib")
	public @ResponseBody
	ResponseContent uploadLib(@RequestParam("file") MultipartFile file) {
		try {
			String name = file.getOriginalFilename();
			byte[] data = file.getBytes();
			scheduleJobService.uploadLib(data, name);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return new ResponseContent(true, "上传成功.");
	}

	@RequestMapping("schedule/remove_lib")
	public @ResponseBody
	ResponseContent removeLib(String name) {
		scheduleJobService.removeLib(name);
		return new ResponseContent(true, "删除成功.");
	}
}
