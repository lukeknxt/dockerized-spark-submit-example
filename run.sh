mkdir -p data
docker run \
    -v $PWD/data:/var/data \
    -p 4040:4040 \
    dockerize-spark-submit-example:latest \
    spark-submit \
    --master "local[*]" \
    --conf "spark.driver.bindAddress=127.0.0.1" \
    --conf "spark.driver.host=127.0.0.1" \
    /app/dockerize-spark-submit-example.jar net.lukeknight.Main
