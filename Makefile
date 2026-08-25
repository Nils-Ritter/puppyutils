mod:
	@echo "Building client..."
	@./gradlew build

all: mod

clean:
	rm -rf build .gradle

.PHONY: all clean
