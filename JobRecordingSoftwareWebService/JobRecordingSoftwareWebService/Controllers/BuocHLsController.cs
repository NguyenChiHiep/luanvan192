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
    builder.EntitySet<BuocHL>("BuocHLs");
    builder.EntitySet<NguyenCongHL>("NguyenCongHLs"); 
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class BuocHLsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/BuocHLs
        [EnableQuery]
        public IQueryable<BuocHL> GetBuocHLs()
        {
            return db.BuocHLs;
        }

        // GET: odata/BuocHLs(5)
        [EnableQuery]
        public SingleResult<BuocHL> GetBuocHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.BuocHLs.Where(buocHL => buocHL.Id == key));
        }

        // PUT: odata/BuocHLs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<BuocHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BuocHL buocHL = db.BuocHLs.Find(key);
            if (buocHL == null)
            {
                return NotFound();
            }

            patch.Put(buocHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BuocHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(buocHL);
        }

        // POST: odata/BuocHLs
        public IHttpActionResult Post(BuocHL buocHL)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.BuocHLs.Add(buocHL);
            db.SaveChanges();

            return Created(buocHL);
        }

        // PATCH: odata/BuocHLs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<BuocHL> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BuocHL buocHL = db.BuocHLs.Find(key);
            if (buocHL == null)
            {
                return NotFound();
            }

            patch.Patch(buocHL);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BuocHLExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(buocHL);
        }

        // DELETE: odata/BuocHLs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            BuocHL buocHL = db.BuocHLs.Find(key);
            if (buocHL == null)
            {
                return NotFound();
            }

            db.BuocHLs.Remove(buocHL);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/BuocHLs(5)/NguyenCongHL
        [EnableQuery]
        public SingleResult<NguyenCongHL> GetNguyenCongHL([FromODataUri] int key)
        {
            return SingleResult.Create(db.BuocHLs.Where(m => m.Id == key).Select(m => m.NguyenCongHL));
        }

        // GET: odata/BuocHLs(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.BuocHLs.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool BuocHLExists(int key)
        {
            return db.BuocHLs.Count(e => e.Id == key) > 0;
        }
    }
}
