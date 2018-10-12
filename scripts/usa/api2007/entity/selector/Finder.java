package scripts.usa.api2007.entity.selector;

import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.types.generic.Filter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Laniax
 */
public abstract class Finder<T extends Clickable07, S> extends FinderResult<T> implements Supplier<S> {

	protected final List<Filter<T>> filters;
	protected final List<Comparator<T>> comparators;

	protected Finder() {
		filters = new ArrayList<Filter<T>>();
		comparators = new ArrayList<Comparator<T>>();
	}

	protected Filter<T> buildFilter() {
		Filter<T> result = null;

		for (Filter<T> filter : filters) {
			if (result == null) {
				result = filter;
				continue;
			}
			result = result.combine(filter, true);
		}

		return result;
	}

	protected Comparator<T> buildComparator() {
		Comparator<T> result = null;

		for (Comparator<T> comparator : comparators) {
			if (result == null) {
				result = comparator;
				continue;
			}
			result = comparator.thenComparing(comparator);
		}

		return result;
	}

	protected boolean shouldSort() {
		return comparators.size() > 0;
	}

	/**
	 * Apply a lambda as a custom filter. Example usage:
	 * {@code .custom((obj) -> obj.isOnScreen()} Which would only returns obj's
	 * that are on screen.
	 * 
	 * @param lambda
	 *            <T> the lambda to execute
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S custom(Predicate<T> lambda) {
		filters.add(new Filter<T>() {
			@Override
			public boolean accept(T entity) {
				return lambda.test(entity);
			}
		});

		return (S) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public S get() {
		return (S) this;
	}
}
