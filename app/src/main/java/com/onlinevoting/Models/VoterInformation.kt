package com.sunayanpradhan.onlinevoting.Models

data class VoterInformation(var aadhaarId:String,
                            var voterId:String,
                            var phoneNo:String,
                            var userId:String
)
{
    constructor():this(
        "",
        "",
        "",
        ""
    )
}
