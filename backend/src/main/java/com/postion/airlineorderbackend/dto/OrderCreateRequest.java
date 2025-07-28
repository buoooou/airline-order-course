package com.postion.airlineorderbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求数据传输对象
 * 用于创建新订单时的请求参数
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    
    /**
     * 用户ID - 必须提供，用于关联用户
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 航班信息ID - 必须提供，用于关联航班
     */
    @NotNull(message = "航班信息ID不能为空")
    private Long flightInfoId;
    
    /**
     * 乘客数量 - 默认为1
     */
    @Positive(message = "乘客数量必须大于0")
    private Integer passengerCount = 1;
    
    /**
     * 乘客姓名列表 - 用于出票
     */
    private List<String> passengerNames;
    
    /**
     * 联系电话 - 必须提供，用于联系
     */
    @NotNull(message = "联系电话不能为空")
    @Size(max = 11, message = "联系电话长度不能超过11个字符")
    private String contactPhone;
    
    /**
     * 座位偏好 - 可选，如窗口、过道等
     */
    @Size(max = 50, message = "座位偏好长度不能超过50个字符")
    private String seatPreference;
    
    /**
     * 特殊需求 - 可选，如餐食、轮椅等
     */
    @Size(max = 500, message = "特殊需求长度不能超过500个字符")
    private String specialRequirements;
    
    /**
     * 备注信息 - 可选
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;
    
    /**
     * 联系邮箱 - 可选，用于发送确认邮件
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String contactEmail;
    
    /**
     * 紧急联系人姓名 - 可选
     */
    @Size(max = 100, message = "紧急联系人姓名长度不能超过100个字符")
    private String emergencyContactName;
    
    /**
     * 紧急联系人电话 - 可选
     */
    @Size(max = 11, message = "紧急联系人电话长度不能超过11个字符")
    private String emergencyContactPhone;
    
    /**
     * 是否需要发票 - 默认为false
     */
    private Boolean needInvoice = false;
    
    /**
     * 发票抬头 - 如果需要发票则必填
     */
    @Size(max = 200, message = "发票抬头长度不能超过200个字符")
    private String invoiceTitle;
    
    /**
     * 发票税号 - 如果需要发票则必填
     */
    @Size(max = 50, message = "发票税号长度不能超过50个字符")
    private String invoiceTaxNumber;
    
    /**
     * 验证发票信息的完整性
     * @return 发票信息是否完整
     */
    public boolean isInvoiceInfoComplete() {
        if (!Boolean.TRUE.equals(this.needInvoice)) {
            return true; // 不需要发票时认为信息完整
        }
        
        return this.invoiceTitle != null && !this.invoiceTitle.trim().isEmpty() &&
               this.invoiceTaxNumber != null && !this.invoiceTaxNumber.trim().isEmpty();
    }
    
    /**
     * 验证乘客基本信息的完整性
     * @return 乘客基本信息是否完整
     */
    public boolean isPassengerInfoComplete() {
        return this.passengerNames != null && !this.passengerNames.isEmpty() &&
               this.contactPhone != null && !this.contactPhone.trim().isEmpty();
    }
    
    /**
     * 获取乘客信息摘要
     * @return 乘客信息摘要
     */
    public String getPassengerSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (this.passengerNames != null && !this.passengerNames.isEmpty()) {
            summary.append("乘客: ").append(String.join(", ", this.passengerNames));
        }
        
        if (this.contactPhone != null && !this.contactPhone.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("电话: ").append(this.contactPhone);
        }
        
        if (this.contactEmail != null && !this.contactEmail.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("邮箱: ").append(this.contactEmail);
        }
        
        return summary.toString();
    }
}
