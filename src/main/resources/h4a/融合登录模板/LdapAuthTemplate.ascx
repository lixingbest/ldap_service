<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="LdapAuthTemplate.ascx.cs" Inherits="ChinaCustoms.Applications.Cupaa.Webs.Passport.Template.LdapAuthTemplate" %>
<asp:ScriptManager ID="ScriptManager1" runat="server">
</asp:ScriptManager>
<table id="loginTB" border="0" cellspacing="0" cellpadding="0" style="display:none;margin-left: 3px; width: 467px;">
    <tr>
        <td align="center" valign="top" style="background: #0359a2;">
            <table width="467" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="24" height="32">&nbsp;
                    </td>
                    <td width="105" align="left" valign="bottom">
                        <%--用户名：--%>
                        <asp:Literal ID="LitUserName" runat="server"
                            Text="用户名："></asp:Literal>
                    </td>
                    <td width="106" align="left" valign="bottom">
                        <%--密&nbsp;&nbsp;码：--%>
                        <asp:Literal ID="LitPassWord" runat="server"
                            Text="密&nbsp;&nbsp;码："></asp:Literal>
                    </td>
                    <td height="32">&nbsp;
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;
                    </td>
                    <td height="27" align="left" valign="bottom">
                        <asp:TextBox ID="signInName" ClientIDMode="Static" CssClass="text"
                            Style="width: 90px;" validatetype="3"
                            runat="server" meta:resourcekey="signInNameResource1"></asp:TextBox>
                    </td>
                    <td align="left" valign="bottom" id="tdpassword">
                        <asp:TextBox ID="password" ClientIDMode="Static" CssClass="text" Style="width: 90px; ime-mode: isabled"
                            runat="server" EnableViewState="False" TextMode="Password" validatetype="1"></asp:TextBox>
                    </td>
                    <td align="right" valign="bottom">
                        <asp:Button ID="SignInButton" runat="server" OnClick="SignInButton_Click" Text="登&nbsp;&nbsp;录" Style="background-color: #44a0f7;color:white;text-align:center;width:55px;height:23px;border:0;cursor:pointer;"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="4">
                        <asp:Label ID="errorMessage" ClientIDMode="Static" runat="server" ForeColor="Red" Font-Bold="True"
                            Style="line-height: 150%" meta:resourcekey="errorMessageResource1"></asp:Label>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<script type="text/javascript">

    /**
     * 请求kerberos登录
     * 注意：尽管函数中使用了一些IE8不支持的特性，但此函数仅在Linux系统下运行
     */
     function reqKerberos(){

        var win = window.open("hyldap://whoami");
        win.onbeforeunload = function(){
            setTimeout(function(){
                $.get("http://127.0.0.1:8888", function(data) {

                    var text = atob(data);
                    var resp = JSON.parse(text);
                    if(resp.success){
                        var username = resp.name;

                        $("#signInName").val(username);
                        $("#password").val("KERBEROS");
                        $("input[type=submit]").click();
                    }else{

                        $("#errorMessage").text("当前用户不是LDAP用户，切换到ldap表单登录");
                        $("#loginTB").slideDown();
                    }
                })
            },500)
        }
    }

    // 页面加载时触发
    $(document).ready(function () {

        // 检测系统类型，如果是linux，则优先使用kerberos登录，其他平台默认使用form登录
        var userAgent = navigator.userAgent.toLowerCase();

        // 如果是linux系统，则优先使用kerberos自动登录，与ldap agent通信
        if(userAgent.indexOf("linux") != -1){

            $("#errorMessage").text("LDAP Kerberos 自动登录中...");

            reqKerberos();
        }else if(userAgent.indexOf("windows") != -1){

            // 如果是windows系统，则自动切换到AD域认证选项
            document.getElementById("logonType").value="windowsauthentication";
            logonTypeChange(document.getElementById("logonType"));
        }else if( (userAgent.match(/MicroMessenger/i) == 'micromessenger') && (userAgent.match(/wxwork/i) == 'wxwork') ){

            // 如果是企业微信客户端，则自动切换到微信认证选项
            document.getElementById("logonType").value="wechatauthentication";
            logonTypeChange(document.getElementById("logonType"));
        }else{

            // 其他系统，默认使用表单登录
            $("#loginTB").slideDown();
        }
    });

</script>
