using ChinaCustoms.Applications.Cupaa.Webs.Passport.CommonCS;
using ChinaCustoms.Frameworks.Cupaa.Libraries.Passport.Common;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace WebApplication1
{
    public partial class logontypetemp : CupaaLoginUserControl
    {

        // todo: 需要配置在配置文件中
        const string ldapUrl = "http://10.98.5.184:8080/service/public_service/user/validate";
        const string sKey = "A3F2569DESJEIWBCJOTY45DYQWF68H1Y";
        const string sIV = "qcDY6X+aPLw=";
        
        protected void Page_Load(object sender, EventArgs e)
        {
                
        }

        /// <summary>
        /// 进行表单账号和密码验证
        /// </summary>
        /// <param name="strLogonName">ipasession</param>
        /// <returns></returns>
        private static bool DoFormAuthentication(string strLogonName, string strPassword, out string strErrorMessage)
        {
            strErrorMessage = "";

            // 是否为kerberos登录
            if (strPassword != null && strPassword == "KERBEROS") {
                strErrorMessage += "Kerberos登录成功！";
                return true;
            }

            if (strLogonName == null || strLogonName.Length == 0) {
                strErrorMessage += "用户名不能为空！";
                return false;
            }

            if (strPassword == null || strPassword.Length == 0) {
                strErrorMessage += "密码不能为空！";
                return false;
            }

            try{

                IDictionary<string, string> parameters = new Dictionary<string, string>();
                parameters.Add("user", HttpUtility.UrlEncode(strLogonName));
                parameters.Add("password", HttpUtility.UrlEncode(EncryptString(strPassword)));
                string url = ConfigurationManager.AppSettings["ldapUrl"];
                // 发送请求
                HttpWebResponse res = CreatePostHttpResponse(url, parameters, 30000);
                if (res == null)
                {
                    strErrorMessage += "请求时网络出现错误！";
                }
                else
                {
                    // 解析请求
                    string mes = GetResponseString(res);
                    if (mes.IndexOf("\"success\":true") > 0)
                    {
                        strErrorMessage += "用户验证成功";
                        // 验证成功，请执行后续操作
                        return true;
                    }
                    else
                    {
                        // 验证失败，需要赋值给errorMessage字段
                        strErrorMessage += "登录凭证验证失败！";
                    }
                }
            }catch (Exception e){
                strErrorMessage += e.ToString();
            }
    
            return false;
        }

        /// <summary>
        /// 创建Post请求
        /// <param name="url"></param>
        /// <param name="parameters"></param>
        /// <param name="timeout"></param>
        /// </summary>
        public static HttpWebResponse CreatePostHttpResponse(string url, IDictionary<string, string> parameters, int timeout)
        {
            HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.Timeout = timeout;

            if (!(parameters == null || parameters.Count == 0))
            {
                StringBuilder buffer = new StringBuilder();
                int i = 0;
                foreach (string key in parameters.Keys)
                {
                    if (i > 0)
                    {
                        buffer.AppendFormat("&{0}={1}", key, parameters[key]);
                    }
                    else
                    {
                        buffer.AppendFormat("{0}={1}", key, parameters[key]);
                        i++;
                    }
                }
                byte[] data = Encoding.ASCII.GetBytes(buffer.ToString());
                using (Stream stream = request.GetRequestStream())
                {
                    stream.Write(data, 0, data.Length);
                }
            }
            return request.GetResponse() as HttpWebResponse;
        }

        /// <summary>
        /// 获取请求响应
        /// <param name="webresponse"></param>
        /// </summary>
        public static string GetResponseString(HttpWebResponse webresponse)
        {
            using (Stream s = webresponse.GetResponseStream())
            {
                StreamReader reader = new StreamReader(s, Encoding.UTF8);
                return reader.ReadToEnd();
            }
        }

        /// <summary>
        /// 加密字符串
        /// <param name="Value"></param>
        /// </summary>
        /// public  string EncryptString(string Value) modify by 武继蛟
        public static string EncryptString(string Value)
        {
            ICryptoTransform ct;
            MemoryStream ms;
            CryptoStream cs;
            byte[] byt;
            //在方法中创建对象  modify by 武继蛟
            SymmetricAlgorithm mCSP = new TripleDESCryptoServiceProvider();
            mCSP.Key = Convert.FromBase64String(sKey);
            mCSP.IV = Convert.FromBase64String(sIV);
            mCSP.Mode = System.Security.Cryptography.CipherMode.ECB;
            mCSP.Padding = System.Security.Cryptography.PaddingMode.PKCS7;
            ct = mCSP.CreateEncryptor(mCSP.Key, mCSP.IV);
            byt = Encoding.UTF8.GetBytes(Value);
            ms = new MemoryStream();
            cs = new CryptoStream(ms, ct, CryptoStreamMode.Write);
            cs.Write(byt, 0, byt.Length);
            cs.FlushFinalBlock();
            cs.Close();
            return Convert.ToBase64String(ms.ToArray());
        }

        /// <summary>
        /// 登录按钮点击事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void SignInButton_Click(object sender, EventArgs e)
        {

            DataTable dtTemplateTableInfo = PassPortCacheHelper.GetTemplateTableInfoInCache(SsoCommon.TemplateTableInfo, cacheKeyGuid);
            DataRow[] drList = dtTemplateTableInfo.Select("HasPass=0");
            string strBaseMode = drList[0]["BaseMode"].ToString().ToLower(CultureInfo.InvariantCulture);
            string strLogonName = this.signInName.Text;
            string strPassword = this.password.Text;
            string strErrorMessage;
            if (DoFormAuthentication(strLogonName, strPassword, out strErrorMessage))
            {
                if (SsoCommon.CheckIsOnePerson(strLogonName, strBaseMode, cacheKeyGuid, out strErrorMessage))
                {
                    //更新认证信息
                    SsoCommon.RemoveCurrentBasedMode(strLogonName, cacheKeyGuid, 1);

                    //是否还有待认证的基本认证方式
                    if (!SsoCommon.CheckIsHasBaseMode(cacheKeyGuid))
                    {

                        SsoCommon.AddSsoLoginLog(cacheKeyGuid);
                        //没有，执行返回应用操作
                        SsoCommon.DoBackAction(cacheKeyGuid);
                    }
                    else
                    {
                        //有，重新加载当前页面
                        if (Request.Url.ToString().IndexOf("&" + SsoCommon.CacheKeyParam + "=") > 0)
                        {
                            Response.Redirect(Request.Url.ToString(), false);
                        }
                        else
                        {
                            Response.Redirect(Request.Url.ToString().TrimEnd('&') + "&" + SsoCommon.CacheKeyParam + "=" + this.cacheKeyGuid, false);
                        }
                    }
                }
                else
                {
                    SsoCommon.AddSsoLoginFailedLog(strLogonName + " & " + strBaseMode);
                    SsoCommon.CheckSsoLoginTimes(cacheKeyGuid, this.Page, "");
                    if (!string.IsNullOrEmpty(strErrorMessage))
                    {
                        errorMessage.Text = strErrorMessage;
                    }
                }
            }
            else
            {
                if (!string.IsNullOrEmpty(strErrorMessage))
                {
                    errorMessage.Text = strErrorMessage;
                }
            }
        }
    }

}