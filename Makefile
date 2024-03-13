# Define the variables:
DESTDIR=./dist
JAVA_OPTS=
MVN_CMD=mvn

# Define the targets:
all: build test clean

# define the rules for each target:
build: 
	@echo "Building project..."
	$(MVN_CMD) package -DskipTests $(JAVA_OPTS)

test: build
	@echo "Running tests..."
	$(MVN_CMD) test $(JAVA_OPTS)

clean:
	@echo "Cleaning project..."
	rm -rf target/*.jar
