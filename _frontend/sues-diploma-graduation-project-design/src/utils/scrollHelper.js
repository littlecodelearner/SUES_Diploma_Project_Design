/**
 * Page scrolling utility functions for automatic scrolling after pagination
 */

/**
 * Scroll to a specific element position
 * @param {HTMLElement|string} element - HTML element or element selector
 * @param {Object} options - Scroll options
 * @param {number} options.offset - Top offset, defaults to 0
 * @param {string} options.behavior - Scrolling behavior, defaults to 'smooth'
 * @returns {Promise<void>} Returns a Promise that resolves when scrolling is complete
 */
export function scrollToElement(element, options = {}) {
  // Default options
  const defaultOptions = {
    offset: 0,
    behavior: 'smooth'
  };
  
  // Merge options
  const mergedOptions = { ...defaultOptions, ...options };
  
  return new Promise((resolve) => {
    // If string selector is provided, get the corresponding DOM element
    let targetElement = element;
    if (typeof element === 'string') {
      targetElement = document.querySelector(element);
    }
    
    // If element not found, resolve the Promise immediately
    if (!targetElement) {
      console.warn('Scroll target element not found:', element);
      resolve();
      return;
    }
    
    // Get element position relative to viewport
    const rect = targetElement.getBoundingClientRect();
    
    // Calculate scroll distance (relative to current scroll position)
    const scrollTop = rect.top + window.scrollY - mergedOptions.offset;
    
    // Execute scroll
    window.scrollTo({
      top: scrollTop,
      behavior: mergedOptions.behavior
    });
    
    // Determine when to resolve the Promise based on scroll behavior
    if (mergedOptions.behavior === 'smooth') {
      // Smooth scrolling takes time, resolve after about 500ms
      setTimeout(resolve, 500);
    } else {
      // Immediate scroll, resolve right away
      resolve();
    }
  });
}

/**
 * Pagination scroll helper function for scrolling when pagination changes
 * @param {HTMLElement|string} element - HTML element or element selector to scroll to
 * @param {Object} options - Scroll options
 * @returns {Function} Returns a function that can be called when pagination changes
 */
export function createPaginationScrollHandler(element, options = {}) {
  return async () => {
    // Add a small delay to ensure data has been updated
    await new Promise(resolve => setTimeout(resolve, 10));
    // Execute scroll
    await scrollToElement(element, options);
  };
}
