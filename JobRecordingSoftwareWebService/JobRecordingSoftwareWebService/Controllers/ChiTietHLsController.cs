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
    builder.EntitySet<ChiTietHL>("ChiTietHLs");
    builder.EntitySet<CongViec>("CongViecs"); 
    builder.EntitySet<NguyenCongHL>("NguyenCongHLs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ChiTietHLsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ChiTietHLs
        [EnableQuery]
        public IQueryable<ChiTietHL> GetChiTietHLs()
        {
            return db.ChiTietHLs;
        }

        // GET: odata/ChiTietHLs(5)
        [EnableQuery]
        public SingleResult<ChiTietHL> GetChiTietHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.ChiTietHLs.Where(chiTietHL => chiTietHL.Id == key));
        }

        // PUT: odata/ChiTietHLs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ChiTietHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ChiTietHL chiTietHL = db.ChiTietHLs.Find(key);
            if (chiTietHL == null)
            {
                return NotFound();
            }

            patch.Put(chiTietHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ChiTietHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(chiTietHL);
        }

        // POST: odata/ChiTietHLs
        public IHttpActionResult Post(ChiTietHL chiTietHL)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ChiTietHLs.Add(chiTietHL);
            db.SaveChanges();

            return Created(chiTietHL);
        }

        // PATCH: odata/ChiTietHLs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ChiTietHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ChiTietHL chiTietHL = db.ChiTietHLs.Find(key);
            if (chiTietHL == null)
            {
                return NotFound();
            }

            patch.Patch(chiTietHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ChiTietHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(chiTietHL);
        }

        // DELETE: odata/ChiTietHLs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ChiTietHL chiTietHL = db.ChiTietHLs.Find(key);
            if (chiTietHL == null)
            {
                return NotFound();
            }

            db.ChiTietHLs.Remove(chiTietHL);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ChiTietHLs(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.ChiTietHLs.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        // GET: odata/ChiTietHLs(5)/NguyenCongHLs
        [EnableQuery]
        public IQueryable<NguyenCongHL> GetNguyenCongHLs([FromODataUri] int key)
        {
            return db.ChiTietHLs.Where(m => m.Id == key).SelectMany(m => m.NguyenCongHLs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ChiTietHLExists(int key)
        {
            return db.ChiTietHLs.Count(e => e.Id == key) > 0;
        }
    }
}
