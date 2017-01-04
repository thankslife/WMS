package com.wms.controller;

import com.wms.bean.Invoice;
import com.wms.bean.Shipment;
import com.wms.commons.base.BaseController;
import com.wms.commons.bean.Search;
import com.wms.commons.utils.PageInfo;
import com.wms.commons.utils.ReadXls;
import com.wms.commons.utils.StringUtils;
import com.wms.commons.utils.TimeUtils;
import com.wms.service.ShipmentService;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实际出库表
 * Created by ZhongYu on 2016/12/19.
 */
@Controller
@RequestMapping(value = "shipment")
public class ShipmentController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    @Autowired
    private ShipmentService shipmentService;

    /** 出货单管理页面 */
    @GetMapping(value = "shipment.html")
    public String getShipmentPage() {
        return "outbound/shipment";
    }

    /** 分页查询 */
    @ResponseBody
    @PostMapping("dataGrid")
    public Object dataGrid(Search search, Integer page, Integer rows) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String, Object> condition = new HashMap<String, Object>();
        logger.info("出库表分页查询");
        if (StringUtils.isNotBlank(search.getName())) {
            condition.put("name", search.getName());
        }
        if (search.getStartTime() != null) {
            condition.put("startTime", search.getStartTime());
        }
        if (search.getEndTime() != null) {
            condition.put("endTime", search.getEndTime());
        }
        pageInfo.setCondition(condition);
        shipmentService.selectDataGrid(pageInfo);
        return pageInfo;
    }

    /** 查询所有实际出库表 */
    @ResponseBody
    @GetMapping(value = "shipment")
    public List<Shipment> queryAll() {
        return shipmentService.queryAll();
    }

    /** 进入添加页面 */
    @GetMapping("shipment/insert")
    public String getAddShipmentPage() {
        return "outbound/shipmentAdd";
    }

    /** 添加操作 */
    @ResponseBody
    @PostMapping(value = "shipment")
    public Object addShipment(Shipment shipment, String addtime) {
        shipment.setShTime(TimeUtils.updateTime(addtime));
        int result = shipmentService.addShipment(shipment);
        if (result > 0) {
            return renderSuccess("添加成功!");
        } else {
            return renderError("添加失败!");
        }
    }

    /** 删除操作 */
    @ResponseBody
    @PostMapping(value = "shipment/delete")
    public Object deleteById(Integer id) {
        int result = shipmentService.deleteById(id);
        if (result > 0) {
            return renderSuccess("删除成功!");
        } else {
            return renderError("删除失败!");
        }
    }

    /** 进入编辑页面 */
    @GetMapping(value = "getEditPage")
    public String editPage(Model model, @RequestParam(value = "id") Integer id) {
        Shipment shipment = shipmentService.selectById(id);
        model.addAttribute("shipment", shipment);
        return "outbound/shipmentEdit";
    }

    /** 更新操作 */
    @ResponseBody
    @PostMapping(value = "shipment/update")
    public Object updateShipment(Shipment shipment, String addtime) {
        shipment.setShTime(TimeUtils.updateTime(addtime));
        int result = shipmentService.updateShipment(shipment);
        if (result > 0) {
            return renderSuccess("更新成功!");
        } else {
            return renderError("更新失败!");
        }
    }

    @GetMapping("importShipment.html")
    public String importPage(){
        return "outbound/shipmentImport";
    }

    /** 读取提交的Excel */
    @PostMapping("readExcle")
    public String readExcle(@RequestParam("file") MultipartFile file, Model model) {
        String path = (AllotoutController.class.getResource("/").toString()).substring(6);
        if (!file.isEmpty()) {
            try {
                Streams.copy(file.getInputStream(), new FileOutputStream(path + "/" + file.getOriginalFilename()), true);
                URL url = ShipmentController.class.getResource("/" + file.getOriginalFilename());
                List<List<String>> lists = ReadXls.readxls(url.getFile());
                Shipment shipment = new Shipment();
                List<String> objects = lists.get(2);
                for (int i = 0; i < objects.size(); i++) {
                    //货主
                    shipment.setShStoreid(objects.get(0));
                    //时间
                    shipment.setShTime(TimeUtils.updateTime("".equals(objects.get(1)) ? null : objects.get(1)));
                    //号码
                    shipment.setShPhone(objects.get(2));
                    //单号
                    shipment.setShSippingno(objects.get(3));
                    //仓库
                    shipment.setShWhid(objects.get(4));
                    //损坏数量
                    shipment.setShDamage(Integer.valueOf(objects.get(5)));
                    //损坏原因
                    shipment.setShCause(objects.get(6));
                    //型号
                    shipment.setShSkumodel(objects.get(7));
                    //数量
                    shipment.setShShnum(Double.valueOf(objects.get(8)));
                    //毛重
                    shipment.setShTotalweigh(Double.valueOf(objects.get(9)));
                    //体积
                    shipment.setShTotalvolume(Double.valueOf(objects.get(10)));
                }
                model.addAttribute("shipment", shipment);
            } catch (Exception e) {
                model.addAttribute("error", "请导入正确的数据!!!");
                e.printStackTrace();
            }
        }
        return "outbound/shipmentImport";
    }

    @ResponseBody
    @PostMapping(value = "shipment.php")
    public Object importCrossDatabase(Shipment shipment, String byTime){
        shipment.setShTime(TimeUtils.updateTime(byTime));
        int result = shipmentService.importShipment(shipment);
        if (result > 0) {
            return renderSuccess("添加成功!");
        }
        return renderError("添加失败!");
    }

}