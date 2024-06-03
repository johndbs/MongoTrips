# Readme
This is a simple project with MongoDb and Java.

# Run
To run this project you need to have a MongoDb instance running.

Add the vm option `-Dmongodb.uri=mongodb://localhost:27017/test` to the run configuration.

# MongoDb request

Use the Database 
    
```json
use MongoTrips
```

To clear the database you can use the following command:

```json
db.customers.deleteMany({})
db.bookings.deleteMany({})
db.trips.deleteMany({})
```


The aggregation is like this:

```json
db.customers.aggregate([
  {
    $match: { "_id" : ObjectId("6659c19ae1a42d4dc4907a34") }
  },
  {
    $lookup: {
      from: "bookings",
      localField: "_id",
      foreignField: "customerId",
      as: "bookings"
    }
  },
  {
    $unwind: "$bookings"
  },
  {
    $lookup: {
      from: "trips",
      localField: "bookings.tripId",
      foreignField: "_id",
      as: "bookings.tripDetails"
    }
  },
  {
    $unwind: "$bookings.tripDetails"
  },
  {
    $group: {
      _id: "$_id",
      firstName: { $first: "$firstName" },
      lastName: { $first: "$lastName" },
      email: { $first: "$email" },
      phone: { $first: "$phone" },
      address: { $first: "$address" },
      bookings: { $push: "$bookings" }
    }
  },
  {
    $project: {
      _id: 1,
      firstName: 1,
      lastName: 1,
      email: 1,
      phone: 1,
      address: 1,
      bookings: 1
    }
  }
])
```

