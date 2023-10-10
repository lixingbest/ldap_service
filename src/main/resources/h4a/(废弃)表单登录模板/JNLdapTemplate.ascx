<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="JNLdapTemplate.ascx.cs" Inherits="ChinaCustoms.Applications.Cupaa.Webs.Passport.Template.JNLdapTemplate" %>
<asp:ScriptManager ID="ScriptManager1" runat="server">
</asp:ScriptManager>
<table id="loginTB" border="0" cellspacing="0" cellpadding="0" style="margin-left: 3px; width: 467px;">
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
                        <asp:TextBox ID="password" CssClass="text" Style="width: 90px; ime-mode: isabled"
                            runat="server" EnableViewState="False" TextMode="Password" validatetype="1"></asp:TextBox>
                    </td>
                    <td align="right" valign="bottom">
                        <asp:Button ID="SignInButton" runat="server" OnClick="SignInButton_Click" Text="登&nbsp;&nbsp;录" Style="background-color: #44a0f7;color:white;text-align:center;width:55px;height:23px;border:0;cursor:pointer;"/>
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