package com.sunayanpradhan.onlinevoting.Models

data class HelplineInformation(
    var hlContext:String,
    var hlLogo:String,
    var hlName:String
)
{


    constructor():this(
        "",
        "",
        ""
    )
}
