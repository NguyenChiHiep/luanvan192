using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Routing;
using OneDuyKhanhDataAccess;

namespace JobRecordingSoftwareWebService.Controllers
{
    /*
    The WebApiConfig class may require additional changes to add a route for this controller. Merge these statements into the Register method of the WebApiConfig class as applicable. Note that OData URLs are case sensitive.

    using System.Web.Http.OData.Builder;
    using System.Web.Http.OData.Extensions;
    using OneDuyKhanhDataAccess;
    ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
    builder.EntitySet<ThongKeCongViecTrongNgay>("ThongKeCongViecTrongNgays");
    builder.EntitySet<CaLamViec>("CaLamViecs"); 
    builder.EntitySet<NhanVien>("NhanViens"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ThongKeCongViecTrongNgaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ThongKeCongViecTrongNgays
        [EnableQuery]
        public IQueryable<ThongKeCongViecTrongNgay> GetThongKeCongViecTrongNgays()
        {
            return db.ThongKeCongViecTrongNgays;
        }

        // GET: odata/ThongKeCongViecTrongNgays(5)
        [EnableQuery]
        public SingleResult<ThongKeCongViecTrongNgay> GetThongKeCongViecTrongNgay([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThongKeCongViecTrongNgays.Where(thongKeCongViecTrongNgay => thongKeCongViecTrongNgay.Id == key));
        }

        // PUT: odata/ThongKeCongViecTrongNgays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ThongKeCongViecTrongNgay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThongKeCongViecTrongNgay thongKeCongViecTrongNgay = db.ThongKeCongViecTrongNgays.Find(key);
            if (thongKeCongViecTrongNgay == null)
            {
                return NotFound();
            }

            patch.Put(thongKeCongViecTrongNgay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThongKeCongViecTrongNgayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thongKeCongViecTrongNgay);
        }

        // POST: odata/ThongKeCongViecTrongNgays
        public IHttpActionResult Post(ThongKeCongViecTrongNgay thongKeCongViecTrongNgay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ThongKeCongViecTrongNgays.Add(thongKeCongViecTrongNgay);
            db.SaveChanges();

            return Created(thongKeCongViecTrongNgay);
        }

        // PATCH: odata/ThongKeCongViecTrongNgays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ThongKeCongViecTrongNgay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThongKeCongViecTrongNgay thongKeCongViecTrongNgay = db.ThongKeCongViecTrongNgays.Find(key);
            if (thongKeCongViecTrongNgay == null)
            {
                return NotFound();
            }

            patch.Patch(thongKeCongViecTrongNgay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThongKeCongViecTrongNgayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thongKeCongViecTrongNgay);
        }

        // DELETE: odata/ThongKeCongViecTrongNgays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ThongKeCongViecTrongNgay thongKeCongViecTrongNgay = db.ThongKeCongViecTrongNgays.Find(key);
            if (thongKeCongViecTrongNgay == null)
            {
                return NotFound();
            }

            db.ThongKeCongViecTrongNgays.Remove(thongKeCongViecTrongNgay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ThongKeCongViecTrongNgays(5)/CaLamViec
        [EnableQuery]
        public SingleResult<CaLamViec> GetCaLamViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThongKeCongViecTrongNgays.Where(m => m.Id == key).Select(m => m.CaLamViec));
        }

        // GET: odata/ThongKeCongViecTrongNgays(5)/NhanVien1
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien1([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThongKeCongViecTrongNgays.Where(m => m.Id == key).Select(m => m.NhanVien1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ThongKeCongViecTrongNgayExists(int key)
        {
            return db.ThongKeCongViecTrongNgays.Count(e => e.Id == key) > 0;
        }
    }
}
