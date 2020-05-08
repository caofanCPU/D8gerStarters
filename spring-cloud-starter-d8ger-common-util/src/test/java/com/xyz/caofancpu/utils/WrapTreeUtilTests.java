package com.xyz.caofancpu.utils;

import com.xyz.caofancpu.core.JSONUtil;
import com.xyz.caofancpu.core.WrapTreeUtil;
import com.xyz.caofancpu.extra.CollectorGenerateUtil;
import com.xyz.caofancpu.extra.NormalUseForTestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 树型结构处理工具类测试用例
 *
 * @author D8GER
 */
public class WrapTreeUtilTests {

    public static void main(String[] args) {
        testInitOriginTreeByPid();
    }

    public static void testExpandSubTreeElements() {
        List<Area> nonNestedList = buildAreaList();
        List<Area> resultNonNestedList = WrapTreeUtil.expandSubTreeElements(nonNestedList, 10, Area::getPid, Area::getId, Area::setChildren, Area::getChildren, Function.identity(), Area::getSortNo);
        NormalUseForTestUtil.out("子树平铺结果\n" + JSONUtil.formatStandardJSON(resultNonNestedList));
    }

    /**
     * 使用情况: 在一些第三方接口中返回的字段用'_'而非驼峰, 因而产生树[Area]转换为树[Area]的需求
     * 根据pid装换树
     */
    public static void testInitTreeByPid() {
        List<Area> areaList = buildAreaNestedList();
        List<Area> nonNestedList = WrapTreeUtil.expandNonNestedTreeList(areaList, Area::getPid, Area::getId, Area::setChildren, Area::getChildren, Function.identity(), Area::getSortNo);
        List<Area> resultTreeList = WrapTreeUtil.initOriginTreeByPid(nonNestedList, Area::getPid, Area::getId, Area::setChildren, Area::getSortNo);
        NormalUseForTestUtil.out("转换树\n" + JSONUtil.formatStandardJSON(resultTreeList));
    }

    public static void testInitOriginTreeByPid() {
        List<Area> nonNestedList = buildAreaList();
        List<Area> originTreeList = WrapTreeUtil.initOriginTreeByPid(nonNestedList, Area::getPid, Area::getId, Area::setChildren, Area::getSortNo);
        NormalUseForTestUtil.out("转换原始元素树\n" + JSONUtil.formatStandardJSON(originTreeList));
    }

    public static void testCutTreeElementByDepth() {
        List<Area> areaList = buildAreaNestedList();
        List<Area> resultList = WrapTreeUtil.cutTreeElementByDepth(areaList, 3, Area::getChildren, Area::getDepth);
        NormalUseForTestUtil.out("嵌套列表树按照深度裁剪结果\n" + JSONUtil.formatStandardJSON(resultList));
    }

    public static void testSelectTreeLeafElements() {
        List<Area> areaList = buildAreaNestedList();
        List<Area> resultList = WrapTreeUtil.selectRelativeTreeLeafByDepth(areaList, 2, Area::getChildren, Area::getDepth, Function.identity());
        NormalUseForTestUtil.out("嵌套列表树按照深度选择相对叶子节点及其父节点结果\n" + JSONUtil.formatStandardJSON(resultList));
    }

    public static void testPureSelectTreeLeafElements() {
        List<Area> areaList = buildAreaNestedList();
        List<Area> resultList = WrapTreeUtil.pureSelectRelativeTreeLeafByDepth(areaList, 1, Area::getPid, Area::getId, Area::setChildren, Area::getChildren, Area::getDepth, Function.identity(), Area::getSortNo);
        NormalUseForTestUtil.out("嵌套列表树按照深度选择相对叶子节点及其父节点(叶子节点子集置空)结果\n" + JSONUtil.formatStandardJSON(resultList));
        int a = 1;
    }

    public static void testExpandTreeElements() {
        List<Area> areaList = buildAreaList();
        List<Area> resultList = WrapTreeUtil.expandNonNestedTreeList(areaList, Area::getPid, Area::getId, Area::setChildren, Area::getChildren, Function.identity(), Area::getSortNo);
        NormalUseForTestUtil.out("平铺列表树平铺结果\n" + JSONUtil.formatStandardJSON(resultList));
    }

    public static void testCollectTreeLeafElements() {
        List<Area> areaList = buildAreaList();
        List<Area> resultList = WrapTreeUtil.collectRelativeTreeLeafElements(areaList, 1, Area::getPid, Area::getId, Area::setChildren, Area::getChildren, Area::getDepth, Function.identity(), Area::getSortNo);
        NormalUseForTestUtil.out("平铺列表树按照深度选择相对叶子节点及其父节点结果结果\n" + JSONUtil.formatStandardJSON(resultList));
        int a = 1;
    }

    public static List<Area> buildAreaList() {
        Area beijingProvince = new Area(2, "北京市", 0, 1, 3);

        Area sichuanProvince = new Area(3, "四川省", 0, 1, 1);
        Area chengduCity = new Area(30, "成都市", 3, 2, 1);

        Area hubeiProvince = new Area(1, "湖北省", 0, 1, 2);
        Area wuhanCity = new Area(10, "武汉市", 1, 2, 2);
        Area xiangyangCity = new Area(11, "襄阳市", 1, 2, 1);
        Area hongshanCounty = new Area(100, "洪山区", 10, 3, 1);

        List<Area> areaList = CollectorGenerateUtil.initArrayList(list -> {
            list.add(beijingProvince);
            list.add(sichuanProvince);
            list.add(chengduCity);
            list.add(hubeiProvince);
            list.add(wuhanCity);
            list.add(xiangyangCity);
            list.add(hongshanCounty);
            return list;
        });
        areaList.sort(Comparator.comparing(Area::getPid).reversed());
        return areaList;
    }

    public static List<Area> buildAreaNestedList() {
        Area beijingProvince = new Area(2, "北京市", 0, 1, 3);

        Area sichuanProvince = new Area(3, "四川省", 0, 1, 1);
        Area chengduCity = new Area(30, "成都市", 3, 2, 1);
        sichuanProvince.setChildren(CollectorGenerateUtil.initArrayList(children -> {
            children.add(chengduCity);
            children.sort(Comparator.comparing(Area::getPid).reversed());
            return children;
        }));

        Area hubeiProvince = new Area(1, "湖北省", 0, 1, 2);
        Area wuhanCity = new Area(10, "武汉市", 1, 2, 2);
        Area xiangyangCity = new Area(11, "襄阳市", 1, 2, 1);
        Area hongshanCounty = new Area(100, "洪山区", 10, 3, 1);
        wuhanCity.setChildren(CollectorGenerateUtil.initArrayList(children -> {
            children.add(hongshanCounty);
            children.sort(Comparator.comparing(Area::getPid).reversed());
            return children;
        }));
        hubeiProvince.setChildren(CollectorGenerateUtil.initArrayList(children -> {
            children.add(wuhanCity);
            children.add(xiangyangCity);
            children.sort(Comparator.comparing(Area::getPid).reversed());
            return children;
        }));

        List<Area> areaList = CollectorGenerateUtil.initArrayList(list -> {
            list.add(beijingProvince);
            list.add(sichuanProvince);
            list.add(hubeiProvince);
            return list;
        });
        areaList.sort(Comparator.comparing(Area::getPid).reversed());
        return areaList;
    }

    /**
     * 区域对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    private static class Area implements Serializable {

        /**
         * ID
         */
        private Integer id;

        /**
         * 名称
         */
        private String name;

        /**
         * pid
         */
        private Integer pid;

        /**
         * 节点深度
         */
        private Integer depth;

        /**
         * 节点相对排序值
         */
        private Integer sortNo;

        /**
         * 子节点集合
         */
        private List<Area> children;

        public Area(Integer id, String name, Integer pid) {
            this.id = id;
            this.name = name;
            this.pid = pid;
        }

        public Area(Integer id, String name, Integer pid, Integer depth, Integer sortNo) {
            this.id = id;
            this.name = name;
            this.pid = pid;
            this.depth = depth;
            this.sortNo = sortNo;
        }
    }
}
