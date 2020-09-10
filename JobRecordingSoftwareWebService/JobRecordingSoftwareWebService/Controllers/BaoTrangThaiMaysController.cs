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
    builder.EntitySet<BaoTrangThaiMay>("BaoTrangThaiMays");
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class BaoTrangThaiMaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/BaoTrangThaiMays
        [EnableQuery]
        public IQueryable<BaoTrangThaiMay> GetBaoTrangThaiMays()
        {
            return db.BaoTrangThaiMays;
        }

        // GET: odata/BaoTrangThaiMays(5)
        [EnableQuery]
        public SingleResult<BaoTrangThaiMay> GetBaoTrangThaiMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.BaoTrangThaiMays.Where(baoTrangThaiMay => baoTrangThaiMay.Id == key));
        }

        // PUT: odata/BaoTrangThaiMays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<BaoTrangThaiMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BaoTrangThaiMay baoTrangThaiMay = db.BaoTrangThaiMays.Find(key);
            if (baoTrangThaiMay == null)
            {
                return NotFound();
            }

            patch.Put(baoTrangThaiMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BaoTrangThaiMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(baoTrangThaiMay);
        }

        // POST: odata/BaoTrangThaiMays
        public IHttpActionResult Post(BaoTrangThaiMay baoTrangThaiMay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.BaoTrangThaiMays.Add(baoTrangThaiMay);
            db.SaveChanges();

            return Created(baoTrangThaiMay);
        }

        // PATCH: odata/BaoTrangThaiMays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<BaoTrangThaiMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BaoTrangThaiMay baoTrangThaiMay = db.BaoTrangThaiMays.Find(key);
            if (baoTrangThaiMay == null)
            {
                return NotFound();
            }

            patch.Patch(baoTrangThaiMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BaoTrangThaiMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(baoTrangThaiMay);
        }

        // DELETE: odata/BaoTrangThaiMays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            BaoTrangThaiMay baoTrangThaiMay = db.BaoTrangThaiMays.Find(key);
            if (baoTrangThaiMay == null)
            {
                return NotFound();
            }

            db.BaoTrangThaiMays.Remove(baoTrangThaiMay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool BaoTrangThaiMayExists(int key)
        {
            return db.BaoTrangThaiMays.Count(e => e.Id == key) > 0;
        }
    }
}
