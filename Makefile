# Define the variables:
DESTDIR=./dist
JAVA_OPTS=
MVN_CMD=mvn

all: build test clean

# Build the application to the $DESTDIR folder
build: 
	@echo "Building project..."
	$(MVN_CMD) install -DskipTests $(JAVA_OPTS)
	mkdir -p $(DESTDIR)
	mv ./target/*.jar $(DESTDIR)/
	echo "Binary built to $(DESTDIR)"

# Build and test the application
test: build
	@echo "Running tests..."
	$(MVN_CMD) test $(JAVA_OPTS)

# Clean the project folder, removing built binaries
clean:
	@echo "Cleaning project..."
	$(MVN_CMD) clean

help:
	@awk '/^#/{c=substr($$0,3);next}c&&/^[[:alpha:]][[:alnum:]_-]+:/{print substr($$1,1,index($$1,":")),c}1{c=0}' $(MAKEFILE_LIST) | column -s: -t
