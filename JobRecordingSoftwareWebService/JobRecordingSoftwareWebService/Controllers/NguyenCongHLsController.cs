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
    builder.EntitySet<NguyenCongHL>("NguyenCongHLs");
    builder.EntitySet<BuocHL>("BuocHLs"); 
    builder.EntitySet<ChiTietHL>("ChiTietHLs"); 
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class NguyenCongHLsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/NguyenCongHLs
        [EnableQuery]
        public IQueryable<NguyenCongHL> GetNguyenCongHLs()
        {
            return db.NguyenCongHLs;
        }

        // GET: odata/NguyenCongHLs(5)
        [EnableQuery]
        public SingleResult<NguyenCongHL> GetNguyenCongHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongHLs.Where(nguyenCongHL => nguyenCongHL.Id == key));
        }

        // PUT: odata/NguyenCongHLs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<NguyenCongHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NguyenCongHL nguyenCongHL = db.NguyenCongHLs.Find(key);
            if (nguyenCongHL == null)
            {
                return NotFound();
            }

            patch.Put(nguyenCongHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NguyenCongHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nguyenCongHL);
        }

        // POST: odata/NguyenCongHLs
        public IHttpActionResult Post(NguyenCongHL nguyenCongHL)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.NguyenCongHLs.Add(nguyenCongHL);
            db.SaveChanges();

            return Created(nguyenCongHL);
        }

        // PATCH: odata/NguyenCongHLs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<NguyenCongHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NguyenCongHL nguyenCongHL = db.NguyenCongHLs.Find(key);
            if (nguyenCongHL == null)
            {
                return NotFound();
            }

            patch.Patch(nguyenCongHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NguyenCongHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nguyenCongHL);
        }

        // DELETE: odata/NguyenCongHLs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            NguyenCongHL nguyenCongHL = db.NguyenCongHLs.Find(key);
            if (nguyenCongHL == null)
            {
                return NotFound();
            }

            db.NguyenCongHLs.Remove(nguyenCongHL);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/NguyenCongHLs(5)/BuocHLs
        [EnableQuery]
        public IQueryable<BuocHL> GetBuocHLs([FromODataUri] int key)
        {
            return db.NguyenCongHLs.Where(m => m.Id == key).SelectMany(m => m.BuocHLs);
        }

        // GET: odata/NguyenCongHLs(5)/ChiTietHL
        [EnableQuery]
        public SingleResult<ChiTietHL> GetChiTietHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.NguyenCongHLs.Where(m => m.Id == key).Select(m => m.ChiTietHL));
        }

        // GET: odata/NguyenCongHLs(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.NguyenCongHLs.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool NguyenCongHLExists(int key)
        {
            return db.NguyenCongHLs.Count(e => e.Id == key) > 0;
        }
    }
}
