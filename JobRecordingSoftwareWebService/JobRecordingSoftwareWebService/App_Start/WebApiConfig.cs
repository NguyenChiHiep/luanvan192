using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using OneDuyKhanhDataAccess;
using System.Web.Http.OData.Builder;
using System.Web.Http.OData.Extensions;
using Microsoft.Data.Edm;
using Microsoft.Data.Edm.Csdl;


namespace JobRecordingSoftwareWebService
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            ODataModelBuilder builder = new ODataConventionModelBuilder();
            Version odataVersion1 = new Version(1, 0);
            Version odataVersion3 = new Version(3, 0);
            builder.DataServiceVersion = odataVersion1;
            builder.MaxDataServiceVersion = odataVersion3;

            builder.EntitySet<NhanVien>("NhanViens");
            builder.EntitySet<CongViec>("CongViecs");
            builder.EntitySet<CongViecPhu>("CongViecPhus");
            builder.EntitySet<CongViec2Set>("CongViec2Set");
            builder.EntitySet<May>("Mays");
            builder.EntitySet<BaoTrangThaiMay>("BaoTrangThaiMays");
            builder.EntitySet<ChiTiet>("ChiTiets");
            builder.EntitySet<ChiTietHL>("ChiTietHLs");
            builder.EntitySet<NguyenCong>("NguyenCongs");
            builder.EntitySet<NguyenCongHL>("NguyenCongHLs");
            builder.EntitySet<CaLamViec>("CaLamViecs");
            builder.EntitySet<BuocHL>("BuocHLs");
            builder.EntitySet<HinhDinhKem>("HinhDinhKems");
            builder.EntitySet<ToSX>("ToSXes");
            builder.EntitySet<LenhSanXuat>("LenhSanXuats");
            builder.EntitySet<ThongKeCongViecTrongNgay>("ThongKeCongViecTrongNgays");

            IEdmModel edmModel = builder.GetEdmModel();
            edmModel.SetEdmVersion(odataVersion1);
            edmModel.SetEdmxVersion(odataVersion1);
            // Web API routes
            config.MapHttpAttributeRoutes();
            config.Routes.MapODataServiceRoute("odata", null, edmModel);
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}