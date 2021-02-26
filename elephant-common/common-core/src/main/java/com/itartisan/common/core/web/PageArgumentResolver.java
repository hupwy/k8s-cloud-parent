package com.itartisan.common.core.web;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.List;

public class PageArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 当前记录起始索引
     */
    private final String PAGE_NUM = "pageNum";
    /**
     * 每页显示记录数
     */
    private final String PAGE_SIZE = "pageSize";
    /**
     * 排序列
     */
    private final String ORDER_BY_COLUMN = "orderByColumn[]";
    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    private final String ORDER_DESC = "orderDesc[]";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return null != parameter.getParameterAnnotation(PageSolver.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        Page<?> page = new Page<>();
        // 当前记录起始索引
        if (!StringUtils.isEmpty(req.getParameter(PAGE_NUM))) {
            int current = Integer.parseInt(req.getParameter(PAGE_NUM));
            page.setCurrent(current);
        }
        // 每页显示记录数
        if (!StringUtils.isEmpty(req.getParameter(PAGE_SIZE))) {
            int size = Integer.parseInt(req.getParameter(PAGE_SIZE));
            page.setSize(size);
        }
        // 排序列&方向
        if (!StringUtils.isEmpty(req.getParameter(ORDER_BY_COLUMN)) && !StringUtils.isEmpty(req.getParameter(ORDER_DESC))) {
            ResolvableType resolvableType = ResolvableType.forType(parameter.getGenericParameterType());
            Class<?> domainClass = resolvableType.getGeneric(0).getRawClass();
            String[] orderKeys = req.getParameter(ORDER_BY_COLUMN).split(",");
            String[] orderTypes = req.getParameter(ORDER_DESC).split(",");
            List<OrderItem> orderItemList = Lists.newArrayListWithCapacity(orderKeys.length);
            for (int i = 0; i < orderKeys.length; i++) {
                String orderKey = orderKeys[i];
                Field orderFiled = ReflectionUtils.findField(domainClass, orderKey);
                String orderColumn = orderFiled.getAnnotation(TableField.class).value();
                if ("true".equals(orderTypes[i])) {
                    orderItemList.add(OrderItem.desc(orderColumn));
                } else {
                    orderItemList.add(OrderItem.asc(orderColumn));
                }
            }
            page.setOrders(orderItemList);
        }


        return page;
    }
}
