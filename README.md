# Dockerized `spark-submit` Example

Submitting Spark applications is typically very environmentally dependent, as the Spark application is often packaged up as a JAR without _all_ of the dependencies it needs, expecting the class path provided by `spark-submit` to contain the rest. The "rest", typically includes Spark and Hadoop JARs, but might include other libraries like [`delta`](https://github.com/delta-io/delta). In addition to dependency availability, the dependency versioning can also be problematic, such as between Spark / Hadoop versions. Providing a stable environment via a Docker image can eliminate a lot of these problems, which can be convenient in certain situations.

This is an example of how you can ship a basic dockerized Spark application that only has Docker as a prerequisite.

The example application just writes some CSV data partitions into `/var/data/data.csv`.

## Prerequisites

1. Docker

## Building

```
docker build -t dockerized-spark-submit-example
```

This has been provided as a `build.sh` for convenience.

## Running

The `Dockerfile` is setup in such a way that this will just run.

Presuming you have built the image and tagged it accordingly, then you can run the following.

```
docker run dockerize-spark-submit-example:latest
```

However this will write data in the container. To write the data to the host, we can create a directory `$PWD/data` as a bind-mount at `/var/data`.

Here's an example of `docker run` with the bind-mount.

```
docker run -v $PWD/data:/var/data dockerize-spark-submit-example:latest 
```

See `run.sh` for a full version of a `run` specifying a full user-provided `spark-submit` command with arguments.

## Dockerfile Explanation

We want to provide a container image that has

1. Spark (libraries and executables)
2. Java
3. Our application

The `Dockerfile` contains a multi-stage build with two stages, where the first stage provides us with an area to build our application, to reduce the size of the final image.

1. The first stage:

    1. We use a base image that will support packaging our JAR with `sbt`. 
    1. We download `wget` so we can download a Spark distribution into the image.
    1. We download the Spark distribution and unarchive it into `/opt/spark`.
    1. We run `sbt package` to package our app as a thin JAR, which will write into `target/scala-2.12/<artifact-name>.jar` as per our `build.sbt` file. If we had more dependencies besides just Spark, we might package up as an uber JAR with [`sbt assembly`](https://github.com/sbt/sbt-assembly) for convenience.

1. The second stage:

    1. At this point we have Spark available and our application packaged, now we just setup the final image by copying it all over onto a basic Java image.
    1. We install [`tini`](https://github.com/krallin/tini) as a general best practice for signal handling.
    1. We setup some environment variables for convenience when running our app.
    1. We expose port 4040 for the Spark UI.
    1. We copy over the Spark distribution, and the app JAR.
    1. We set our entrypoint as `tini` to provide a base for running your own variation of `spark-submit` if needed.
    1. We set the default `CMD` to run `spark-submit`, referencing our app JAR and main-class, as well as the `MASTER` provided as an environment variable that the user could configure if desired. The user can then either override the `MASTER` or provide their own entire `spark-submit` command.
        - The reason we use shell form (`sh -c`) here is so we can get shell variable expansion for the `MASTER` environment variable. In exec form, Docker won't use a shell to execute `CMD` so variable expansion won't work. See the docs [here](https://docs.docker.com/engine/reference/builder/#cmd).