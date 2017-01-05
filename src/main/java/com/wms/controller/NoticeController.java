package com.wms.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wms.bean.vo.UserVo;
import com.wms.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.wms.bean.Notice;
import com.wms.commons.base.BaseController;
import com.wms.commons.utils.PageInfo;
import com.wms.service.NoticeService;

/**
 * Created by W on 2016/12/23.
 */
@Controller
@RequestMapping("/notice")
public class NoticeController  extends BaseController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private IUserService userService;

    @GetMapping("/manager")
    public String manager() {
        return "system/notice";
    }


    //查询
    @PostMapping("/select")
    @ResponseBody
    public Object select(Notice notice, Integer page, Integer rows) {
        PageInfo pageinfo = new PageInfo(page, rows);
        Map<String, Object> map = new HashMap<String, Object>();
        pageinfo.setCondition(map);
        noticeService.selectDataGrid(pageinfo);
        return pageinfo;
    }





    //添加
    @PostMapping("/add")
    @ResponseBody
    public Object add(Notice notice, @RequestParam(value = "user") String user) {
        UserVo userVo= userService.selectByloginname(user);
        notice.setUserid(userVo.getId().intValue());
        int a = noticeService.insert(notice);
        if(a>0){
            return renderSuccess("添加成功");
        }
        return renderError("添加失败");
    }

    //打开添加页面
    @RequestMapping("/addPage")
    public String addPage(){
        return "system/noticeAdd";
    }


    //编辑 回显
    @GetMapping("/editPage")
    public String editPage(Integer id, Model model) {
        Notice notice = noticeService.selectByPrimaryKey(id);
        System.out.println(notice.getNid());
        model.addAttribute("notice", notice);
        return "system/noticeEdit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public Object edit(Notice notice){

        int a = noticeService.updateByPrimaryKey(notice);
        if(a>0){
            return renderSuccess("修改成功");
        }
        return renderError("修改失败");
    }

    @PostMapping("/delete")
    @ResponseBody
    public Object delete(int id){
        int a  = noticeService.deleteByPrimaryKey(id);
        if(a>0){
            return renderSuccess("删除成功");
        }
        return  renderError("修改失败");
    }

}