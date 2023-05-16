package com.sunayanpradhan.onlinevoting.Models

data class TeamInformation(
    var teamId:String,
    var teamName:String,
    var teamLogo:String,
    var teamSlogan:String,
    var teamColor:String
)
{
    constructor():this(
        "",
        "",
        "",
        "",
        ""
    )
}