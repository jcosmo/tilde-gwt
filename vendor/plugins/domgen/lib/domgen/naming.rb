module Domgen
  module Naming
    def self.camelize(camel_cased_word)
      word_parts = split_into_words(camel_cased_word).collect{|part| part[0...1].upcase + part[1..-1]}
      return word_parts[0].downcase if (word_parts.size == 1 && word_parts[0] == word_parts[0].upcase)
      word = word_parts.join('')
      word = word[0...1].downcase + word[1..-1]
      word
    end

    def self.underscore(camel_cased_word)
      word = split_into_words(camel_cased_word).join("_")
      word.downcase!
      word
    end

    def self.split_into_words(camel_cased_word)
      word = camel_cased_word.to_s.dup
      word.gsub!(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
      word.gsub!(/([a-z\d])([A-Z])/, '\1_\2')
      word.tr!("-", "_")
      word.split("_")
    end

    def self.uppercase_constantize(camel_cased_word)
      underscore(camel_cased_word).upcase
    end

    def self.xmlize(camel_cased_word)
      underscore(camel_cased_word).tr("_", "-")
    end

    def self.jsonize(camel_cased_word)
      underscore(camel_cased_word)
    end

    def self.pluralize(string)
      plural = nil
      #in case someone passes in a Symbol instead
      singular = string.to_s
      case last(singular)
        when 'y'
          plural = "#{singular.chop}ies" unless last(singular.chop) =~ /[aeiou]/
        when 'o'
          plural = "#{singular}es" if last(singular.chop) =~ /[aeiou]/
        when 's'
          plural = "#{singular}es" if last(singular, 2) == 'ss'
      end
      plural || "#{singular}s"
    end

    private

    def self.last(s, limit = 1)
      return s if limit > s.to_s.size
      s.to_s[-limit, limit]
    end
  end
end