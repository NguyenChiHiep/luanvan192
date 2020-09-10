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
    builder.EntitySet<NguyenCong>("NguyenCongs");
    builder.EntitySet<ChiTiet>("ChiTiets"); 
    builder.EntitySet<CongViec>("CongViecs"); 
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<NhanVien>("NhanViens"); 
    builder.EntitySet<ToSX>("ToSXes"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class NguyenCongsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/NguyenCongs
        [EnableQuery]
        public IQueryable<NguyenCong> GetNguyenCongs()
        {
            return db.NguyenCongs;
        }

        // GET: odata/NguyenCongs(5)
        [EnableQuery]
        public SingleResult<NguyenCong> GetNguyenCong([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(nguyenCong => nguyenCong.Id == key));
        }

        // PUT: odata/NguyenCongs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<NguyenCong> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NguyenCong nguyenCong = db.NguyenCongs.Find(key);
            if (nguyenCong == null)
            {
                return NotFound();
            }

            patch.Put(nguyenCong);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NguyenCongExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nguyenCong);
        }

        // POST: odata/NguyenCongs
        public IHttpActionResult Post(NguyenCong nguyenCong)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.NguyenCongs.Add(nguyenCong);
            db.SaveChanges();

            return Created(nguyenCong);
        }

        // PATCH: odata/NguyenCongs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<NguyenCong> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NguyenCong nguyenCong = db.NguyenCongs.Find(key);
            if (nguyenCong == null)
            {
                return NotFound();
            }

            patch.Patch(nguyenCong);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NguyenCongExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nguyenCong);
        }

        // DELETE: odata/NguyenCongs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            NguyenCong nguyenCong = db.NguyenCongs.Find(key);
            if (nguyenCong == null)
            {
                return NotFound();
            }

            db.NguyenCongs.Remove(nguyenCong);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/NguyenCongs(5)/ChiTiet
        [EnableQuery]
        public SingleResult<ChiTiet> GetChiTiet([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.ChiTiet));
        }

        // GET: odata/NguyenCongs(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.NguyenCongs.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        // GET: odata/NguyenCongs(5)/May
        [EnableQuery]
        public SingleResult<May> GetMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.May));
        }

        // GET: odata/NguyenCongs(5)/NhanVien
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.NhanVien));
        }

        // GET: odata/NguyenCongs(5)/NhanVien1
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien1([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.NhanVien1));
        }

        // GET: odata/NguyenCongs(5)/NhanVien2
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien2([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.NhanVien2));
        }

        // GET: odata/NguyenCongs(5)/ToSX
        [EnableQuery]
        public SingleResult<ToSX> GetToSX([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.ToSX));
        }

        // GET: odata/NguyenCongs(5)/ToSX1
        [EnableQuery]
        public SingleResult<ToSX> GetToSX1([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongs.Where(m => m.Id == key).Select(m => m.ToSX1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool NguyenCongExists(int key)
        {
            return db.NguyenCongs.Count(e => e.Id == key) > 0;
        }
    }
}
