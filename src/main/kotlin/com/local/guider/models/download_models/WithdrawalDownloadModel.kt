package com.local.guider.models.download_models

import java.util.*

class WithdrawalDownloadModel {
    var withdrawal_Id: Long = 0
    var applicant_Id: Long? = null
    var applicant_Name: String? = null
    var applicant_Role: String? = null
    var amount: Double? = null
    var payment_Status: String? = null
    var bank_Name: String? = null
    var account_Number: String? = null
    var account_Holder_Name: String? = null
    var ifsc: String? = null
    var upiId: String? = null
    var created_On: String? = null
}