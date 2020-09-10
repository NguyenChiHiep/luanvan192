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
    builder.EntitySet<LenhSanXuat>("LenhSanXuats");
    builder.EntitySet<ChiTiet>("ChiTiets"); 
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class LenhSanXuatsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/LenhSanXuats
        [EnableQuery]
        public IQueryable<LenhSanXuat> GetLenhSanXuats()
        {
            return db.LenhSanXuats;
        }

        // GET: odata/LenhSanXuats(5)
        [EnableQuery]
        public SingleResult<LenhSanXuat> GetLenhSanXuat([FromODataUri] int key)
        {
            return SingleResult.Create(db.LenhSanXuats.Where(lenhSanXuat => lenhSanXuat.Id == key));
        }

        // PUT: odata/LenhSanXuats(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<LenhSanXuat> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            LenhSanXuat lenhSanXuat = db.LenhSanXuats.Find(key);
            if (lenhSanXuat == null)
            {
                return NotFound();
            }

            patch.Put(lenhSanXuat);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!LenhSanXuatExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(lenhSanXuat);
        }

        // POST: odata/LenhSanXuats
        public IHttpActionResult Post(LenhSanXuat lenhSanXuat)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.LenhSanXuats.Add(lenhSanXuat);
            db.SaveChanges();

            return Created(lenhSanXuat);
        }

        // PATCH: odata/LenhSanXuats(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<LenhSanXuat> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            LenhSanXuat lenhSanXuat = db.LenhSanXuats.Find(key);
            if (lenhSanXuat == null)
            {
                return NotFound();
            }

            patch.Patch(lenhSanXuat);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!LenhSanXuatExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(lenhSanXuat);
        }

        // DELETE: odata/LenhSanXuats(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            LenhSanXuat lenhSanXuat = db.LenhSanXuats.Find(key);
            if (lenhSanXuat == null)
            {
                return NotFound();
            }

            db.LenhSanXuats.Remove(lenhSanXuat);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/LenhSanXuats(5)/ChiTiets
        [EnableQuery]
        public IQueryable<ChiTiet> GetChiTiets([FromODataUri] int key)
        {
            return db.LenhSanXuats.Where(m => m.Id == key).SelectMany(m => m.ChiTiets);
        }

        // GET: odata/LenhSanXuats(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.LenhSanXuats.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        // GET: odata/LenhSanXuats(5)/LenhSanXuats1
        [EnableQuery]
        public IQueryable<LenhSanXuat> GetLenhSanXuats1([FromODataUri] int key)
        {
            return db.LenhSanXuats.Where(m => m.Id == key).SelectMany(m => m.LenhSanXuats1);
        }

        // GET: odata/LenhSanXuats(5)/LenhSanXuat1
        [EnableQuery]
        public SingleResult<LenhSanXuat> GetLenhSanXuat1([FromODataUri] int key)
        {
            return SingleResult.Create(db.LenhSanXuats.Where(m => m.Id == key).Select(m => m.LenhSanXuat1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool LenhSanXuatExists(int key)
        {
            return db.LenhSanXuats.Count(e => e.Id == key) > 0;
        }
    }
}
