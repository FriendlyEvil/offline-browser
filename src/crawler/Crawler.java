package crawler;

import model.Result;

public interface Crawler extends AutoCloseable {
	Result download(String url, int depth);

	@Override
	void close();
}