<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="LdapKerberosTemplate.ascx.cs" Inherits="ChinaCustoms.Applications.Cupaa.Webs.Passport.Template.LdapKerberosTemplate" %>
<asp:ScriptManager ID="ScriptManager1" runat="server">
</asp:ScriptManager>
<table id="loginTB" border="0" cellspacing="0" cellpadding="0" style="margin-left: 3px; width: 467px;">
    <tr>
        <td align="center" valign="top" style="background: #0359a2;">
            <table width="467" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td height="27" align="left" valign="bottom">
                        <asp:TextBox ID="signInName"  ClientIDMode="Static" CssClass="text"
                            Style="width: 90px;display:none;" validatetype="3"
                            runat="server" meta:resourcekey="signInNameResource1"></asp:TextBox>
                    </td>
                    <td align="right" valign="bottom">
                        <asp:Button ID="SignInButton" runat="server" OnClick="SignInButton_Click" Text="登&nbsp;&nbsp;录" Style="display:none;background-color: #44a0f7;color:white;text-align:center;width:55px;height:23px;border:0;cursor:pointer;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="4">
                        <asp:Label ID="errorMessage" runat="server" ForeColor="Red" Font-Bold="True"
                            Style="line-height: 150%" meta:resourcekey="errorMessageResource1"></asp:Label>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<script type="text/javascript"> 

    function reqKerberos(){
        console.log("请求kerberos认证");
        var client = new XMLHttpRequest();
        client.open("GET", "https://dcserver.jn.hg.gov.cn/ipa/session/login_kerberos", true);
        client.withCredentials = true;
        client.send();
        client.onreadystatechange = function() {
            if(this.readyState == 4) {
                if(this.status == 200){
                    let cookie = client.getResponseHeader("ipasession"); 
                    console.log(">>"+cookie);

                    var iclient = new XMLHttpRequest();
                    iclient.open("GET", "http://10.98.4.231:8080/service/public_service/user/whoami?type=js&ipasession="+btoa(cookie), true);
                    iclient.withCredentials = true;
                    iclient.send();
                    iclient.onreadystatechange = function() {
                        if(this.readyState == 4) {
                            if(this.status == 200){
                                
                                let resp = JSON.parse(this.response);
                                if(resp.success){
                                    let username = resp.data.arguments[0];
                                    console.log(">>>" + username);

                                    let cacheData = btoa(JSON.stringify({
                                        "timestamp" : new Date().getTime(),
                                        "username" : username
                                    }));
                                    localStorage.setItem("kerberos_cache", cacheData);

                                    $("#signInName").val(username); 
                                    $("input[type=submit]").click(); 
                                }else{
                                    console.log("validate error!"); 
                                    $("#signInName").val("ERROR"); 
                                    $("input[type=submit]").click(); 
                                }
                            }else{
                                console.log("server error!"); 
                                $("#signInName").val("ERROR"); 
                                $("input[type=submit]").click(); 
                            };
                        }
                    };
                }else{
                    console.log("kerberos error!"); 
                    $("#signInName").val("ERROR"); 
                    $("input[type=submit]").click(); 
                };
            }
        };
    }

    // 页面加载时触发
    $(document).ready(function () {

        console.log("即将检测kerberos cache可用性");
        let kerberosCache = localStorage.getItem("kerberos_cache");
        if(kerberosCache){
            console.log("发现存在目标cache");
            let obj = JSON.parse(atob(kerberosCache));
            let username = obj.username;
            let timestamp = obj.timestamp;
            if(new Date().getTime() - timestamp < 6 * 60 * 60 * 1000){
                console.log("目标cache有效，将自动登录");
                $("#signInName").val(username); 
                $("input[type=submit]").click(); 
            }else{
                console.log("目标已过期，即将删除");
                localStorage.removeItem("kerberos_cache");

                reqKerberos();
            }
        }else{
            console.log("不存在cache，即将请求kerberos");
            reqKerberos();
        }
    });
</script>