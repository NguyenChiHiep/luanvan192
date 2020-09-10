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
    builder.EntitySet<CongViec2Set>("CongViec2Set");
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class CongViec2SetController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/CongViec2Set
        [EnableQuery]
        public IQueryable<CongViec2Set> GetCongViec2Set()
        {
            return db.CongViec2Set;
        }

        // GET: odata/CongViec2Set(5)
        [EnableQuery]
        public SingleResult<CongViec2Set> GetCongViec2Set([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViec2Set.Where(congViec2Set => congViec2Set.Id == key));
        }

        // PUT: odata/CongViec2Set(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<CongViec2Set> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec2Set congViec2Set = db.CongViec2Set.Find(key);
            if (congViec2Set == null)
            {
                return NotFound();
            }

            patch.Put(congViec2Set);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViec2SetExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec2Set);
        }

        // POST: odata/CongViec2Set
        public IHttpActionResult Post(CongViec2Set congViec2Set)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.CongViec2Set.Add(congViec2Set);
            db.SaveChanges();

            return Created(congViec2Set);
        }

        // PATCH: odata/CongViec2Set(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<CongViec2Set> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec2Set congViec2Set = db.CongViec2Set.Find(key);
            if (congViec2Set == null)
            {
                return NotFound();
            }

            patch.Patch(congViec2Set);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViec2SetExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec2Set);
        }

        // DELETE: odata/CongViec2Set(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            CongViec2Set congViec2Set = db.CongViec2Set.Find(key);
            if (congViec2Set == null)
            {
                return NotFound();
            }

            db.CongViec2Set.Remove(congViec2Set);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/CongViec2Set(5)/CongViec
        [EnableQuery]
        public SingleResult<CongViec> GetCongViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViec2Set.Where(m => m.Id == key).Select(m => m.CongViec));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool CongViec2SetExists(int key)
        {
            return db.CongViec2Set.Count(e => e.Id == key) > 0;
        }
    }
}
