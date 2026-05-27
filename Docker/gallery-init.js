db = db.getSiblingDB('gallery_db');

db.createCollection("media", {
  validator: {
    $jsonSchema: {
      bsonType: "object",

      required: [
        "url",
        "publicId",
        "type",
        "year",
        "excursion",
        "folder",
        "createdAt"
      ],

      properties: {

        _id: {
          bsonType: "objectId"
        },

        url: {
          bsonType: "string",
          description: "Cloudinary secure URL"
        },

        publicId: {
          bsonType: "string",
          description: "Cloudinary public identifier"
        },

        type: {
          enum: [
            "IMAGE",
            "VIDEO"
          ],
          description: "Media type"
        },

        year: {
          bsonType: "int",
          minimum: 2000,
          maximum: 2100,
          description: "Excursion year"
        },

        excursion: {
          bsonType: "string",
          minLength: 2,
          maxLength: 100,
          description: "Excursion name"
        },

        location: {
          bsonType: "string",
          description: "Excursion location"
        },

        folder: {
          bsonType: "string",
          description: "Cloudinary folder path"
        },

        createdAt: {
          bsonType: "date",
          description: "Creation date"
        }
      }
    }
  }
});

db.media.createIndex({ year: 1 });

db.media.createIndex({ excursion: 1 });

db.media.createIndex({ location: 1 });

db.media.createIndex({ type: 1 });