package com.sunayanpradhan.onlinevoting.Models

data class VoteResponseInformation (var voterId:String, var voteTeam:String, var voteTime:Long)
{
    constructor():this(
        "",
        "",
        0
    )


}