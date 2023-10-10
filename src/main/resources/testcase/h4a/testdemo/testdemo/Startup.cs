using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Hosting;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace TestDemo
{
    public class Startup
    {

        // todo: 需要配置在配置文件中的
        const string url = "http://192.168.1.54:9000/service/public_service/user/validate";
        const string sKey = "A3F2569DESJEIWBCJOTY45DYQWF68H1Y";
        const string sIV = "qcDY6X+aPLw=";
        SymmetricAlgorithm mCSP = new TripleDESCryptoServiceProvider();

        /// <summary>
        /// 创建Post请求
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
        /// </summary>
        public string EncryptString(string Value)
        {
            ICryptoTransform ct;
            MemoryStream ms;
            CryptoStream cs;
            byte[] byt;
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

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseRouting();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapGet("/", async context =>
                {

                    //todo: user和password为登录表单字段
                    IDictionary<string, string> parameters = new Dictionary<string, string>();
                    parameters.Add("user", HttpUtility.UrlEncode("jn/h4anew"));
                    parameters.Add("password", HttpUtility.UrlEncode(EncryptString("1234567")));
                    // 发送请求
                    HttpWebResponse res = CreatePostHttpResponse(url, parameters, 30000);
                    if (res == null)
                    {
                        await context.Response.WriteAsync("network error!");
                    }
                    else
                    {
                        // 解析请求
                        string mes = GetResponseString(res);
                        if (mes.IndexOf("\"success\":true") > 0)
                        {
                            await context.Response.WriteAsync(mes);
                        }
                        else
                        {
                            await context.Response.WriteAsync("账号验证失败！");
                        }
                    }

                });
            });
        }
    }
}
