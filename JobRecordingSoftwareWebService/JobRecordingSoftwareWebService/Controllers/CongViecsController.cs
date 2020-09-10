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
    builder.EntitySet<CongViec>("CongViecs");
    builder.EntitySet<BuocHL>("BuocHLs"); 
    builder.EntitySet<ChiTietHL>("ChiTietHLs"); 
    builder.EntitySet<ChiTiet>("ChiTiets"); 
    builder.EntitySet<CongViec2Set>("CongViec2Set"); 
    builder.EntitySet<CongViecPhu>("CongViecPhus"); 
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<NguyenCongHL>("NguyenCongHLs"); 
    builder.EntitySet<NhanVien>("NhanViens"); 
    builder.EntitySet<NguyenCong>("NguyenCongs"); 
    builder.EntitySet<LenhSanXuat>("LenhSanXuats"); 
    builder.EntitySet<HinhDinhKem>("HinhDinhKems"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class CongViecsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs()
        {
            return db.CongViecs;
        }

        // GET: odata/CongViecs(5)
        [EnableQuery]
        public SingleResult<CongViec> GetCongViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(congViec => congViec.Id == key));
        }

        // PUT: odata/CongViecs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<CongViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            patch.Put(congViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec);
        }

        // POST: odata/CongViecs
        public IHttpActionResult Post(CongViec congViec)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.CongViecs.Add(congViec);
            db.SaveChanges();

            return Created(congViec);
        }

        // PATCH: odata/CongViecs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<CongViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            patch.Patch(congViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec);
        }

        // DELETE: odata/CongViecs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            db.CongViecs.Remove(congViec);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/CongViecs(5)/BuocHL
        [EnableQuery]
        public SingleResult<BuocHL> GetBuocHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.BuocHL));
        }

        // GET: odata/CongViecs(5)/ChiTietHL
        [EnableQuery]
        public SingleResult<ChiTietHL> GetChiTietHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.ChiTietHL));
        }

        // GET: odata/CongViecs(5)/ChiTiet
        [EnableQuery]
        public SingleResult<ChiTiet> GetChiTiet([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.ChiTiet));
        }

        // GET: odata/CongViecs(5)/CongViec2Set
        [EnableQuery]
        public IQueryable<CongViec2Set> GetCongViec2Set([FromODataUri] int key)
        {
            return db.CongViecs.Where(m => m.Id == key).SelectMany(m => m.CongViec2Set);
        }

        // GET: odata/CongViecs(5)/CongViecPhu
        [EnableQuery]
        public SingleResult<CongViecPhu> GetCongViecPhu([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.CongViecPhu));
        }

        // GET: odata/CongViecs(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.May1));
        }

        // GET: odata/CongViecs(5)/NguyenCongHL
        [EnableQuery]
        public SingleResult<NguyenCongHL> GetNguyenCongHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.NguyenCongHL));
        }

        // GET: odata/CongViecs(5)/NhanVien
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.NhanVien));
        }

        // GET: odata/CongViecs(5)/NguyenCong
        [EnableQuery]
        public SingleResult<NguyenCong> GetNguyenCong([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.NguyenCong));
        }

        // GET: odata/CongViecs(5)/LenhSanXuat1
        [EnableQuery]
        public SingleResult<LenhSanXuat> GetLenhSanXuat1([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.LenhSanXuat1));
        }

        // GET: odata/CongViecs(5)/HinhDinhKems
        [EnableQuery]
        public IQueryable<HinhDinhKem> GetHinhDinhKems([FromODataUri] int key)
        {
            return db.CongViecs.Where(m => m.Id == key).SelectMany(m => m.HinhDinhKems);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool CongViecExists(int key)
        {
            return db.CongViecs.Count(e => e.Id == key) > 0;
        }
    }
}
